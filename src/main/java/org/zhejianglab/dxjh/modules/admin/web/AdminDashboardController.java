package org.zhejianglab.dxjh.modules.admin.web;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.zhejianglab.dxjh.common.dto.PageResult;
import org.zhejianglab.dxjh.modules.admin.dto.AdminCertificateRowDto;
import org.zhejianglab.dxjh.modules.admin.dto.AdminStatsDto;
import org.zhejianglab.dxjh.modules.admin.dto.AdminTrendDto;
import org.zhejianglab.dxjh.modules.admin.service.AdminDashboardService;

import java.util.List;

/**
 * @author :og-twelve
 * @date : 2025/9/8
 */
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminDashboardController {

    private final AdminDashboardService svc;

    @GetMapping("/stats")
    public ResponseEntity<AdminStatsDto> stats() {
        return ResponseEntity.ok(svc.getStats());
    }

    // 可加简单分页入参；前端当前不传就给 200 条
    @GetMapping("/certificates")
    public ResponseEntity<PageResult<AdminCertificateRowDto>> certificates(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size
    ) {
        return ResponseEntity.ok(svc.listCertificates(page, size));
    }


    @GetMapping("/trend")
    public ResponseEntity<AdminTrendDto> trend(
            @RequestParam(value = "days", defaultValue = "7") int days) {
        return ResponseEntity.ok(svc.getTrend(days));
    }

}
