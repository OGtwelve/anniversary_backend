package org.zhejianglab.dxjh.modules.admin.web;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.zhejianglab.dxjh.modules.admin.dto.AdminCertificateRowDto;
import org.zhejianglab.dxjh.modules.admin.dto.AdminCertificateRowDto_OpenDay;
import org.zhejianglab.dxjh.modules.annivcert.dto.UpdateCertificateRequest;
import org.zhejianglab.dxjh.modules.annivcert.dto.UpdateCertificateRequestOpenDay;
import org.zhejianglab.dxjh.modules.annivcert.entity.AnnivCertificate_OpenDay;
import org.zhejianglab.dxjh.modules.annivcert.service.AnnivCertificateService;
import org.zhejianglab.dxjh.modules.annivcert.service.AnnivCertificate_OpenDayService;

/**
 * @author :og-twelve
 * @date : 2025/10/22
 */
@RestController
@RequestMapping("/api/open_day_admin/certificates")
@RequiredArgsConstructor
public class OpenDayCrudController {

    private final AnnivCertificate_OpenDayService crud;

    @PutMapping("/{fullNo}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AdminCertificateRowDto_OpenDay> update(@PathVariable String fullNo,
                                                                 @RequestBody UpdateCertificateRequestOpenDay req) {
        return ResponseEntity.ok(crud.update(fullNo, req));
    }

    @DeleteMapping("/{fullNo}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable String fullNo) {
        crud.delete(fullNo);
        return ResponseEntity.noContent().build();
    }
}
