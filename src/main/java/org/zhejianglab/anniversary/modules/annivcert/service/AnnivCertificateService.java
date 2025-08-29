package org.zhejianglab.anniversary.modules.annivcert.service;


import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zhejianglab.anniversary.modules.annivcert.dto.CertificateDto;
import org.zhejianglab.anniversary.modules.annivcert.entity.*;
import org.zhejianglab.anniversary.modules.annivcert.repository.*;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * @author :og-twelve
 * @date : 2025/8/30
 */
@Service
public class AnnivCertificateService {

    static final LocalDate TARGET = LocalDate.of(2025, 9, 6);
    static final int MAX_TOTAL = 1500;
    static final int SEG_WIDTH = 4;

    final AnnivCertificateRepository certRepo;
    final AnnivScsQuotaRepository quotaRepo;
    final AnnivSeqCounterRepository seqRepo;

    public AnnivCertificateService(AnnivCertificateRepository c, AnnivScsQuotaRepository q, AnnivSeqCounterRepository s) {
        this.certRepo = c; this.quotaRepo = q; this.seqRepo = s;
    }

    @Transactional
    public CertificateDto issue(String name, LocalDate startDate, String workNo, String ip, String ua) {
        // 1) 检查数据库中是否已经存在相同的工号和姓名记录
        AnnivCertificate existingCertificate = certRepo.findByNameAndWorkNo(name, workNo);

        // 如果已有记录，并且入职日期有更新，则更新记录
        if (existingCertificate != null) {
            // 如果 startDate 更新了
            if (!existingCertificate.getStartDate().equals(startDate)) {
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

                // 保存更新后的记录
                certRepo.save(existingCertificate);

                // 返回更新后的证书信息
                return new CertificateDto(existingCertificate.getFullNo(), existingCertificate.getScsCode(),
                        existingCertificate.getDaysToTarget(), name, startDate, workNo);
            } else {
                // 如果没有更新 startDate，直接返回现有证书
                return new CertificateDto(existingCertificate.getFullNo(), existingCertificate.getScsCode(),
                        existingCertificate.getDaysToTarget(), name, existingCertificate.getStartDate(), workNo);
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
        List<AnnivScsQuota> quotas = quotaRepo.findAllByOrderByScsCodeAsc(); // 查找所有组并按 SCS 码排序
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
        c.setIp(ip); c.setUa(ua);

        // 保存证书记录
        certRepo.saveAndFlush(c);

        // 返回证书数据
        return new CertificateDto(fullNo, scs, (int) days, name, startDate, workNo);
    }

    private static String getScs(List<AnnivScsQuota> quotas) {
        AnnivScsQuota selectedQuota = null;
        for (AnnivScsQuota quota : quotas) {
            // 如果组的配额尚未满
            if (quota.getIssued() < 125) {
                selectedQuota = quota;
                break;  // 找到第一个剩余配额未满的组，跳出循环
            }
        }

        // 如果所有组都已满，抛出异常
        if (selectedQuota == null) {
            throw new IllegalStateException("所有组已满");
        }

        // 更新该组的配额已使用人数
        selectedQuota.setIssued(selectedQuota.getIssued() + 1);
        return selectedQuota.getScsCode();
    }

}
