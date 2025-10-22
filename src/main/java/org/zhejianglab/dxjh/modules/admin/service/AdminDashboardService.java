package org.zhejianglab.dxjh.modules.admin.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.zhejianglab.dxjh.common.dto.PageResult;
import org.zhejianglab.dxjh.modules.admin.dto.AdminCertificateRowDto;
import org.zhejianglab.dxjh.modules.admin.dto.AdminCertificateRowDto_OpenDay;
import org.zhejianglab.dxjh.modules.admin.dto.AdminStatsDto;
import org.zhejianglab.dxjh.modules.admin.dto.AdminTrendDto;
import org.zhejianglab.dxjh.modules.annivcert.entity.AnnivCertificate;
import org.zhejianglab.dxjh.modules.annivcert.entity.AnnivCertificate_OpenDay;
import org.zhejianglab.dxjh.modules.annivcert.repository.AnnivCertificateRepository;
import org.zhejianglab.dxjh.modules.annivcert.repository.AnnivCertificate_OpenDayRepository;

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

    private final AnnivCertificate_OpenDayRepository certOpenDayRepo;

    // 与发证逻辑保持一致的目标日
    private static final LocalDate TARGET = LocalDate.of(2025, 9, 6);

    public AdminStatsDto getStats(Class<?> entity) {
        long total = 0;
        LocalDate today = LocalDate.now(ZoneId.systemDefault());
        LocalDateTime start = today.atStartOfDay();
        LocalDateTime end = start.plusDays(1);
        long todayCount = 0;
        double avgYears = 0.0;
        long blessings = 0;
        if (AnnivCertificate.class.equals(entity)) {
            total = certRepo.count();
            todayCount = certRepo.countByCreatedAtBetween(start, end);
            Double avgDays = certRepo.avgWorkDays(TARGET);
            avgYears = 0.0;
            if (avgDays != null) {
                avgYears = Math.round((avgDays / 365.0) * 10.0) / 10.0; // 保留1位小数
            }
            blessings = certRepo.countValidWishes();
        }

        if (AnnivCertificate_OpenDay.class.equals(entity)) {
            total = certOpenDayRepo.count();
            todayCount = certOpenDayRepo.countByCreatedAtBetween(start, end);
            Double avgDays = certOpenDayRepo.avgBirthDays(TARGET);
            avgYears = 0.0;
            if (avgDays != null) {
                avgYears = Math.round((avgDays / 365.0) * 10.0) / 10.0; // 保留1位小数
            }
            blessings = certOpenDayRepo.countValidWishes();
        }

        return new AdminStatsDto(total, todayCount, avgYears, blessings);
    }

    public PageResult<Object> listCertificates(int page, int size, Class<?> entity) {
        DateTimeFormatter d  = DateTimeFormatter.ofPattern("yyyy/M/d");
        DateTimeFormatter dt = DateTimeFormatter.ofPattern("yyyy/M/d HH:mm:ss");

        size = Math.max(1, Math.min(size, 200)); // 上限保护
        page = Math.max(0, page);

        List<Object> items = new ArrayList<>();
        Page<AnnivCertificate> p = Page.empty();
        Page<AnnivCertificate_OpenDay> pOpenDay = Page.empty();

        if (AnnivCertificate.class.equals(entity)) {
            p = certRepo.findPageByOrderByCreatedAtDesc(PageRequest.of(page, size));
            items = p.stream()
                    .map(c -> toRow(c, d, dt))
                    .collect(Collectors.toList());
        }

        if (AnnivCertificate_OpenDay.class.equals(entity)) {
            pOpenDay = certOpenDayRepo.findPageByOrderByCreatedAtDesc(PageRequest.of(page, size));
            items = pOpenDay.stream()
                    .map(c -> toRow(c, d, dt))
                    .collect(Collectors.toList());
        }

        long total = p.hasContent() ? p.getTotalElements() : pOpenDay.getTotalElements();

        return new PageResult<>(items, total, page, size);
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

    private AdminCertificateRowDto_OpenDay toRow(AnnivCertificate_OpenDay c,
                                                    DateTimeFormatter d, DateTimeFormatter dt) {
        String id = c.getFullNo(); // 显示 SCSxx-xxxx-xxxx
        String name = c.getName();
        String joinDate = c.getDateOfBirth() != null ? d.format(c.getDateOfBirth()) : "";
        String blessing = c.getWishes();
        String createdAt = c.getCreatedAt() != null ? dt.format(c.getCreatedAt()) : "";

        return new AdminCertificateRowDto_OpenDay(id, name, joinDate, blessing, createdAt);
    }

    public AdminTrendDto getTrend(int days, Class<?> entity) {
        // 只允许 7/30/90，容错一下
        if (days != 7 && days != 30 && days != 90) days = 7;

        ZoneId zone = ZoneId.systemDefault(); // 或 ZoneId.of("Asia/Shanghai")
        LocalDate today = LocalDate.now(zone);
        LocalDate from = today.minusDays(days - 1);

        // 查库
        List<Object[]> rows = new ArrayList<>();
        if (AnnivCertificate.class.equals(entity)) {
            rows = certRepo.countDaily(
                    from.atStartOfDay(),
                    today.plusDays(1).atStartOfDay()
            );
        }
        if (AnnivCertificate_OpenDay.class.equals(entity)) {
            rows = certOpenDayRepo.countDaily(
                    from.atStartOfDay(),
                    today.plusDays(1).atStartOfDay()
            );
        }

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