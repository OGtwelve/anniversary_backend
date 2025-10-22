package org.zhejianglab.dxjh.modules.annivcert.service;


import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zhejianglab.dxjh.modules.admin.dto.AdminCertificateRowDto;
import org.zhejianglab.dxjh.modules.annivcert.dto.CertificateDto;
import org.zhejianglab.dxjh.modules.annivcert.dto.UpdateCertificateRequest;
import org.zhejianglab.dxjh.modules.annivcert.entity.*;
import org.zhejianglab.dxjh.modules.annivcert.repository.*;
import org.zhejianglab.dxjh.modules.annivquiz.service.AnnivQuizService;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * @author :og-twelve
 * @date : 2025/8/30
 */
@Service
public class AnnivCertificateService {

    static final LocalDate TARGET = LocalDate.of(2025, 9, 6);
    private static final DateTimeFormatter D  = DateTimeFormatter.ofPattern("yyyy/M/d");
    private static final DateTimeFormatter DT = DateTimeFormatter.ofPattern("yyyy/M/d HH:mm:ss");
    // 1) 放开总名额
    static final int MAX_TOTAL = 2000;

    // 每组基础配额（前 12 组 * 125 = 1500）
    static final int GROUP_LIMIT = 125;
    static final int SEG_WIDTH = 4;

    final AnnivCertificateRepository certRepo;
    final AnnivScsQuotaRepository quotaRepo;
    final AnnivSeqCounterRepository seqRepo;

    final AnnivQuizService quizSvc;

    public AnnivCertificateService(AnnivCertificateRepository c, AnnivScsQuotaRepository q,
                                   AnnivSeqCounterRepository s, AnnivQuizService quizSvc) {
        this.certRepo = c; this.quotaRepo = q; this.seqRepo = s; this.quizSvc = quizSvc;
    }

    public List<AnnivCertificate> findAllForExport() {
        return certRepo.findAll(); // 可改成按时间/条件筛选
    }

    @Transactional
    public ResponseEntity<Object> issue(String name, LocalDate startDate, String workNo, String ip, String ua, String passToken, String wishes) {
        // 先消费问卷通行令牌（一次性）
        quizSvc.consumePassToken(passToken, ip, ua);

        // 1) 检查数据库中是否已经存在相同的工号和姓名记录
        AnnivCertificate existingCertificate = certRepo.findByNameAndWorkNo(name, workNo);

        // 如果已有记录，并且入职日期有更新，则更新记录
        if (existingCertificate != null) {
            // 如果 startDate 更新了
            if (!existingCertificate.getStartDate().equals(startDate)) {

                // 先检查入职日期是否在 2025-09-06 之前
                if (startDate.isAfter(TARGET)) {
                    throw new IllegalStateException("入职日期必须是 2025 年 9 月 6 日之前，请重新选择。");
                }

                // 更新 startDate 和相关数据
                existingCertificate.setStartDate(startDate);
                // 计算新的天数（从入职日期到目标日期 2025-09-06）
                long days = Math.max(0, ChronoUnit.DAYS.between(startDate, TARGET));
                existingCertificate.setDaysToTarget((int) days);

                // 更新 fullNo（使用新的天数和序号）
                String daysSeg = String.format("%04d", Math.min((int) days, 9999)); // 保证 4 位数
                String seqSeg = String.format("%04d", existingCertificate.getSeq()); // 保证 4 位数
                String fullNo = existingCertificate.getScsCode() + "-" + daysSeg + "-" + seqSeg;
                existingCertificate.setFullNo(fullNo);  // 更新 fullNo

                // 更新祝福语
                existingCertificate.setWishes(wishes);

                // 保存更新后的记录
                certRepo.save(existingCertificate);

                // 返回更新后的证书信息
                return ResponseEntity.ok(new CertificateDto(existingCertificate.getFullNo(), existingCertificate.getScsCode(),
                        existingCertificate.getDaysToTarget(), name, startDate, workNo, wishes));
            } else {
                return ResponseEntity.ok(new CertificateDto(existingCertificate.getFullNo(), existingCertificate.getScsCode(),
                        existingCertificate.getDaysToTarget(), name, existingCertificate.getStartDate(), workNo, wishes));
            }
        }

        // 2) 如果没有找到相同记录，生成新的证书
        // 获取全局序号（锁定，避免并发问题）
        AnnivSeqCounter counter = seqRepo.lockByName("ANNIV_CERT")
                .orElseThrow(() -> new IllegalStateException("系统未初始化"));
        int next = counter.getLastSeq() + 1;
        if (next > MAX_TOTAL) throw new IllegalStateException("名额已满");
        counter.setLastSeq(next);

        // 3) 选择配额最少的组
//        AnnivScsQuota quota = quotaRepo.findTopByIssuedLessThanOrderByIssuedAscScsCodeAsc(125);
//        if (quota == null) throw new IllegalStateException("所有组已满");
//        quota.setIssued(quota.getIssued() + 1);
//        String scs = quota.getScsCode();

        // 如果需要前125个都在一个卫星下的话, 就用下面的机制
        // 3) 查找所有组，并按顺序逐个组分配
        List<AnnivScsQuota> quotas = quotaRepo.findAllByVersionCodeOrderByScsCodeAsc("1"); // 查找所有组并按 SCS 码排序
        String scs = getScs(quotas);


        // 4) 计算天数（负数按 0 计算）
        long days = Math.max(0, ChronoUnit.DAYS.between(startDate, TARGET));

        // 5) 拼装完整证书号：SCSxx-<天数>-<顺序号>
        String daysSeg = String.format("%04d", Math.min((int) days, 9999)); // 保证 4 位数
        String seqSeg  = String.format("%04d", next); // 保证 4 位数
        String fullNo  = scs + "-" + daysSeg + "-" + seqSeg;

        // 6) 创建证书记录
        AnnivCertificate c = new AnnivCertificate();
        c.setFullNo(fullNo);
        c.setScsCode(scs);
        c.setSeq(next);
        c.setDaysToTarget((int) days);
        c.setName(name);
        c.setStartDate(startDate);
        c.setWorkNo(workNo);
        c.setWishes(wishes);
        c.setIp(ip); c.setUa(ua);

        // 保存证书记录
        certRepo.saveAndFlush(c);

        // 返回证书数据
        return ResponseEntity.ok(new CertificateDto(fullNo, scs, (int) days, name, startDate, workNo, wishes));
    }

    @Transactional
    public AdminCertificateRowDto update(String fullNo, UpdateCertificateRequest req) {
        AnnivCertificate c = certRepo.findByFullNo(fullNo)
                .orElseThrow(() -> new IllegalArgumentException("证书不存在: " + fullNo));

        if (req.getName() != null)       c.setName(req.getName());
        if (req.getEmployeeId() != null) c.setWorkNo(req.getEmployeeId());
        if (req.getBlessing() != null)   c.setWishes(req.getBlessing());

        if (req.getJoinDate() != null) {
            c.setStartDate(req.getJoinDate());
            // 以 joinDate 为准重算 daysToTarget
            int days = (int) (TARGET.toEpochDay() - req.getJoinDate().toEpochDay());
            c.setDaysToTarget(Math.max(days, 0)); // 不为负
        } else if (req.getWorkYears() != null) {
            c.setDaysToTarget(Math.max(req.getWorkYears(), 0));
        }

        // 保存
        c = certRepo.save(c);

        // 返回行 DTO（与列表相同格式）
        String id = c.getFullNo();
        String name = c.getName();
        String empId = c.getWorkNo();
        String joinDate = c.getStartDate() != null ? D.format(c.getStartDate()) : "";
        int workDays = c.getDaysToTarget() != null ? c.getDaysToTarget() : 0;
        String blessing = c.getWishes();
        String createdAt = c.getCreatedAt() != null ? DT.format(c.getCreatedAt().atZone(ZoneId.systemDefault())) : "";

        return new AdminCertificateRowDto(id, name, empId, joinDate, workDays, blessing, createdAt);
    }

    @Transactional
    public void delete(String fullNo) {
        long n = certRepo.deleteByFullNo(fullNo);
        if (n == 0) {
            throw new IllegalArgumentException("证书不存在: " + fullNo);
        }
    }




    private static String getScs(List<AnnivScsQuota> quotas) {
        if (quotas == null || quotas.isEmpty()) {
            throw new IllegalStateException("配额未初始化");
        }

        // 先顺序填满每组（SCS01 -> SCS12）
        for (AnnivScsQuota q : quotas) {
            if (q.getIssued() < GROUP_LIMIT) {
                q.setIssued(q.getIssued() + 1);
                return q.getScsCode();
            }
        }

        // 超过当前配额（1500）后：统一落在最后一条配额
        AnnivScsQuota last = quotas.get(quotas.size() - 1);
        last.setIssued(last.getIssued() + 1); // 允许超过 125，用于统计
        return last.getScsCode();
    }


}
