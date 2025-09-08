package org.zhejianglab.dxjh.modules.admin.web;


import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.zhejianglab.dxjh.modules.admin.dto.AdminCertificateRowDto;
import org.zhejianglab.dxjh.modules.annivcert.dto.UpdateCertificateRequest;
import org.zhejianglab.dxjh.modules.annivcert.service.AnnivCertificateService;

@RestController
@RequestMapping("/api/admin/certificates")
@RequiredArgsConstructor
public class AdminCertificateCrudController {

    private final AnnivCertificateService crud;

    @PutMapping("/{fullNo}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AdminCertificateRowDto> update(@PathVariable String fullNo,
                                                         @RequestBody UpdateCertificateRequest req) {
        return ResponseEntity.ok(crud.update(fullNo, req));
    }

    @DeleteMapping("/{fullNo}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable String fullNo) {
        crud.delete(fullNo);
        return ResponseEntity.noContent().build();
    }
}
