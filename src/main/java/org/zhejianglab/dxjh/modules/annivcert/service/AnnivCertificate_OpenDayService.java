package org.zhejianglab.dxjh.modules.annivcert.service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;

import org.zhejianglab.dxjh.modules.admin.dto.AdminCertificateRowDto_OpenDay;
import org.zhejianglab.dxjh.modules.annivcert.dto.CertificateOpenDayDto;
import org.zhejianglab.dxjh.modules.annivcert.dto.UpdateCertificateRequest;
import org.zhejianglab.dxjh.modules.annivcert.dto.UpdateCertificateRequestOpenDay;
import org.zhejianglab.dxjh.modules.annivcert.entity.AnnivCertificate_OpenDay;
import org.zhejianglab.dxjh.modules.annivcert.entity.AnnivScsQuota;
import org.zhejianglab.dxjh.modules.annivcert.entity.AnnivSeqCounter;
import org.zhejianglab.dxjh.modules.annivcert.repository.AnnivCertificate_OpenDayRepository;
import org.zhejianglab.dxjh.modules.annivcert.repository.AnnivScsQuotaRepository;
import org.zhejianglab.dxjh.modules.annivcert.repository.AnnivSeqCounterRepository;
import org.zhejianglab.dxjh.modules.annivquiz.service.AnnivQuizService;

@Service
public class AnnivCertificate_OpenDayService {

    static final LocalDate TARGET = LocalDate.of(2025, 9, 6);
    private static final DateTimeFormatter D  = DateTimeFormatter.ofPattern("yyyy/M/d");
    private static final DateTimeFormatter DT = DateTimeFormatter.ofPattern("yyyy/M/d HH:mm:ss");

    // 1) 放开总名额
    static final int MAX_TOTAL = 2000;

    // 每组基础配额（前 12 组 * 125 = 1500）
    static final int GROUP_LIMIT = 125;
    static final int SEG_WIDTH = 4;

    private final AnnivCertificate_OpenDayRepository certRepo;
    private final AnnivScsQuotaRepository quotaRepo;
    private final AnnivSeqCounterRepository seqRepo;
    private final AnnivQuizService quizSvc;

    public AnnivCertificate_OpenDayService(AnnivCertificate_OpenDayRepository c,
                                           AnnivScsQuotaRepository q,
                                           AnnivSeqCounterRepository s,
                                           AnnivQuizService quizSvc) {
        this.certRepo = c;
        this.quotaRepo = q;
        this.seqRepo = s;
        this.quizSvc = quizSvc;
    }

    public List<AnnivCertificate_OpenDay> findAllForExport() {
        return certRepo.findAll();
    }

    @Transactional
    public ResponseEntity<Object> issue(String name,
                                                       LocalDate dateOfBirth,
                                                       String ip,
                                                       String ua,
                                                       String passToken,
                                                       String wishes) {
        // 先消费问卷通行令牌（一次性）
//        quizSvc.consumePassToken(passToken, ip, ua);

        // 幂等：同名同生日视为同一份证书
        AnnivCertificate_OpenDay existing = certRepo.findByNameAndDateOfBirth(name, dateOfBirth);
        if (existing != null) {
            if (wishes != null && !Objects.equals(existing.getWishes(), wishes)) {
                existing.setWishes(wishes);
                certRepo.save(existing);
            }
//            int days = calcDays(existing.getDateOfBirth());
            return ResponseEntity.ok(new CertificateOpenDayDto(
                    existing.getFullNo(),
                    existing.getScsCode(),
                    existing.getName(),
                    existing.getDateOfBirth(),
                    existing.getWishes()
            ));
        }

        // 获取全局序号（加锁，避免并发）
        AnnivSeqCounter counter = seqRepo.lockByName("ANNIV_OPEN_DAY")
                .orElseThrow(() -> new IllegalStateException("系统未初始化"));
        int next = counter.getLastSeq() + 1;
        if (next > MAX_TOTAL) throw new IllegalStateException("名额已满");
        counter.setLastSeq(next);
        seqRepo.save(counter); // 显式保存

        // 选择配额组（按顺序填满 SCS01..SCS12，超额落最后一组）
        List<AnnivScsQuota> quotas = quotaRepo.findAllByVersionCodeOrderByScsCodeAsc("2");
        String scs = getScs(quotas); // 事务内脏写，提交时落库

        // 拼装 fullNo
        int days = calcDays(dateOfBirth);
        String fullNo = buildFullNo(scs, days, next);

        // 创建/保存
        AnnivCertificate_OpenDay c = new AnnivCertificate_OpenDay();
        c.setFullNo(fullNo);
        c.setScsCode(scs);
        c.setSeq(next);
        c.setName(name);
        c.setDateOfBirth(dateOfBirth);
        c.setWishes(wishes);
        c.setIp(ip);
        c.setUa(ua);
        certRepo.saveAndFlush(c);

        return ResponseEntity.ok(new CertificateOpenDayDto(
                fullNo, scs, name, dateOfBirth, wishes
        ));
    }

    @Transactional
    public AdminCertificateRowDto_OpenDay update(String fullNo, UpdateCertificateRequestOpenDay req) {
        AnnivCertificate_OpenDay c = certRepo.findByFullNo(fullNo)
                .orElseThrow(() -> new IllegalArgumentException("证书不存在: " + fullNo));

        if (req.getName() != null) {
            c.setName(req.getName());
        }
        if (req.getJoinDate() != null) {
            c.setDateOfBirth(req.getJoinDate());
            // 生日变化 → 重算天数段并重拼 fullNo（SCS/seq 不变）
            int days = calcDays(c.getDateOfBirth());
            c.setFullNo(buildFullNo(c.getScsCode(), days, c.getSeq()));
        }
        if (req.getBlessing() != null) {
            c.setWishes(req.getBlessing());
        }

        c = certRepo.save(c);

//        int workDays = calcDays(c.getDateOfBirth());
        String createdAt = (c.getCreatedAt() != null)
                ? DT.format(c.getCreatedAt().atZone(ZoneId.systemDefault()))
                : "";

        return new AdminCertificateRowDto_OpenDay(
                c.getFullNo(),
                c.getName(),
                D.format(c.getDateOfBirth()),
                c.getWishes(),
                createdAt
        );
    }

    @Transactional
    public void delete(String fullNo) {
        long n = certRepo.deleteByFullNo(fullNo);
        if (n == 0) {
            throw new IllegalArgumentException("证书不存在: " + fullNo);
        }
    }

    private static int calcDays(LocalDate dob) {
        return Math.max(0, (int) ChronoUnit.DAYS.between(dob, TARGET));
    }

    private static String buildFullNo(String scs, int days, int seq) {
        String daysSeg = String.format("%0" + SEG_WIDTH + "d", Math.min(days, 9999));
        String seqSeg  = String.format("%0" + SEG_WIDTH + "d", seq);
//        return scs + "-" + daysSeg + "-" + seqSeg;

        return scs + "-" + "2025" + "-" + seqSeg;
    }

    private static String getScs(List<AnnivScsQuota> quotas) {
        if (quotas == null || quotas.isEmpty()) {
            throw new IllegalStateException("配额未初始化");
        }
        for (AnnivScsQuota q : quotas) {
            if (q.getIssued() < GROUP_LIMIT) {
                q.setIssued(q.getIssued() + 1);
                return q.getScsCode();
            }
        }
        AnnivScsQuota last = quotas.get(quotas.size() - 1);
        last.setIssued(last.getIssued() + 1); // 允许超过 125，用于统计
        return last.getScsCode();
    }
}
