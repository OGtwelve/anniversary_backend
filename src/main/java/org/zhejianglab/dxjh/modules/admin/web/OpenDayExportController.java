package org.zhejianglab.dxjh.modules.admin.web;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.zhejianglab.dxjh.modules.admin.dto.AdminExportRequest;
import org.zhejianglab.dxjh.modules.admin.service.AdminExportService;
import org.zhejianglab.dxjh.modules.annivcert.entity.AnnivCertificate;
import org.zhejianglab.dxjh.modules.annivcert.entity.AnnivCertificate_OpenDay;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author :og-twelve
 * @date : 2025/10/22
 */
@RestController
@RequestMapping("/api/open_day_admin")
@RequiredArgsConstructor
public class OpenDayExportController {

    private final AdminExportService exportSvc;

    @PostMapping("/certificates/export")
    public ResponseEntity<byte[]> export(@RequestBody AdminExportRequest req) throws UnsupportedEncodingException {
        String fmt = (req.getFormat() == null || req.getFormat().isEmpty())
                ? "csv" : req.getFormat().toLowerCase();

        int limit = req.getLimit() == null ? 1000 : Math.min(req.getLimit(), 5000);

        String ts = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String fileName = "证书导出-" + ts + ("xlsx".equals(fmt) ? ".xlsx" : ".csv");

        byte[] data;
        String contentType;

        if ("xlsx".equals(fmt)) {
            // 仍然用 CSV 回退
            data = exportSvc.exportCsv(req.getColumns(), limit, req.getQ(),
                    req.getFromDate(), req.getToDate(), req.getIds(), AnnivCertificate_OpenDay.class);
            contentType = "text/xlsx; charset=UTF-8";
        } else {
            data = exportSvc.exportCsv(req.getColumns(), limit, req.getQ(),
                    req.getFromDate(), req.getToDate(), req.getIds(), AnnivCertificate_OpenDay.class);
            contentType = "text/csv; charset=UTF-8";
        }

        String encoded = URLEncoder.encode(fileName, String.valueOf(StandardCharsets.UTF_8)).replaceAll("\\+", "%20");
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + encoded)
                .header(HttpHeaders.CONTENT_TYPE, contentType)
                .body(data);
    }


}