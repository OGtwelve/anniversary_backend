package org.zhejianglab.dxjh.modules.admin.web;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.zhejianglab.dxjh.common.dto.PageResult;
import org.zhejianglab.dxjh.modules.admin.dto.AdminStatsDto;
import org.zhejianglab.dxjh.modules.admin.dto.AdminTrendDto;
import org.zhejianglab.dxjh.modules.admin.service.AdminDashboardService;
import org.zhejianglab.dxjh.modules.annivcert.entity.AnnivCertificate_OpenDay;

/**
 * @author :og-twelve
 * @date : 2025/10/22
 */
@RestController
@RequestMapping("/api/open_day_admin")
@RequiredArgsConstructor
public class OpenDayDashboardController {

    private final AdminDashboardService svc;

    @GetMapping("/stats")
    public ResponseEntity<AdminStatsDto> stats() {
        return ResponseEntity.ok(svc.getStats(AnnivCertificate_OpenDay.class));
    }

    // 可加简单分页入参；前端当前不传就给 200 条
    @GetMapping("/certificates")
    public ResponseEntity<PageResult<Object>> certificates(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size
    ) {
        return ResponseEntity.ok(svc.listCertificates(page, size, AnnivCertificate_OpenDay.class));
    }


    @GetMapping("/trend")
    public ResponseEntity<AdminTrendDto> trend(
            @RequestParam(value = "days", defaultValue = "7") int days) {
        return ResponseEntity.ok(svc.getTrend(days,AnnivCertificate_OpenDay.class));
    }

}
