package org.zhejianglab.dxjh.modules.admin.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.zhejianglab.dxjh.common.dto.PageResult;
import org.zhejianglab.dxjh.modules.admin.dto.AdminCertificateRowDto;
import org.zhejianglab.dxjh.modules.admin.dto.AdminStatsDto;
import org.zhejianglab.dxjh.modules.admin.dto.AdminTrendDto;
import org.zhejianglab.dxjh.modules.annivcert.entity.AnnivCertificate;
import org.zhejianglab.dxjh.modules.annivcert.repository.AnnivCertificateRepository;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author :og-twelve
 * @date : 2025/9/8
 */
@Service
@RequiredArgsConstructor
public class AdminDashboardService {

    private final AnnivCertificateRepository certRepo;

    // 与发证逻辑保持一致的目标日
    private static final LocalDate TARGET = LocalDate.of(2025, 9, 6);

    public AdminStatsDto getStats() {
        long total = certRepo.count();

        LocalDate today = LocalDate.now(ZoneId.systemDefault());
        LocalDateTime start = today.atStartOfDay();
        LocalDateTime end = start.plusDays(1);
        long todayCount = certRepo.countByCreatedAtBetween(start, end);

        Double avgDays = certRepo.avgWorkDays(TARGET);
        double avgYears = 0.0;
        if (avgDays != null) {
            avgYears = Math.round((avgDays / 365.0) * 10.0) / 10.0; // 保留1位小数
        }

        long blessings = certRepo.countValidWishes();

        return new AdminStatsDto(total, todayCount, avgYears, blessings);
    }

    public PageResult<AdminCertificateRowDto> listCertificates(int page, int size) {
        DateTimeFormatter d  = DateTimeFormatter.ofPattern("yyyy/M/d");
        DateTimeFormatter dt = DateTimeFormatter.ofPattern("yyyy/M/d HH:mm:ss");

        size = Math.max(1, Math.min(size, 200)); // 上限保护
        page = Math.max(0, page);

        Page<AnnivCertificate> p = certRepo.findPageByOrderByCreatedAtDesc(PageRequest.of(page, size));
        List<AdminCertificateRowDto> items = p.stream()
                .map(c -> toRow(c, d, dt))
                .collect(java.util.stream.Collectors.toList());

        return new PageResult<>(items, p.getTotalElements(), page, size);
    }


    private AdminCertificateRowDto toRow(AnnivCertificate c,
                                         DateTimeFormatter d, DateTimeFormatter dt) {
        String id = c.getFullNo(); // 显示 SCSxx-xxxx-xxxx
        String name = c.getName();
        String empId = c.getWorkNo();
        String joinDate = c.getStartDate() != null ? d.format(c.getStartDate()) : "";
        int workDays = c.getDaysToTarget() != null ? c.getDaysToTarget() : 0; // 前端字段叫 workYears
        String blessing = c.getWishes();
        String createdAt = c.getCreatedAt() != null ? dt.format(c.getCreatedAt()) : "";

        return new AdminCertificateRowDto(id, name, empId, joinDate, workDays, blessing, createdAt);
    }

    public AdminTrendDto getTrend(int days) {
        // 只允许 7/30/90，容错一下
        if (days != 7 && days != 30 && days != 90) days = 7;

        ZoneId zone = ZoneId.systemDefault(); // 或 ZoneId.of("Asia/Shanghai")
        LocalDate today = LocalDate.now(zone);
        LocalDate from = today.minusDays(days - 1);

        // 查库
        List<Object[]> rows = certRepo.countDaily(
                from.atStartOfDay(),
                today.plusDays(1).atStartOfDay()
        );

        // 组装为 map<LocalDate, count>
        Map<LocalDate, Long> byDate = new HashMap<>();
        for (Object[] r : rows) {
            // r[0] 是 java.sql.Date，r[1] 是 Number
            LocalDate d = ((java.sql.Date) r[0]).toLocalDate();
            long cnt = ((Number) r[1]).longValue();
            byDate.put(d, cnt);
        }

        // 连续补齐空白日期
        DateTimeFormatter labelFmt = DateTimeFormatter.ofPattern("M月d日");
        List<String> labels = new ArrayList<>(days);
        List<Long> values = new ArrayList<>(days);

        for (LocalDate d = from; !d.isAfter(today); d = d.plusDays(1)) {
            labels.add(labelFmt.format(d));
            values.add(byDate.getOrDefault(d, 0L));
        }

        return new AdminTrendDto(labels, values);
    }

}