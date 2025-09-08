package org.zhejianglab.dxjh.modules.admin.web;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.zhejianglab.dxjh.modules.admin.dto.AdminExportRequest;
import org.zhejianglab.dxjh.modules.admin.service.AdminExportService;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author :og-twelve
 * @date : 2025/9/8
 */
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminExportController {

    private final AdminExportService exportSvc;

    @PostMapping("/certificates/export")
    public ResponseEntity<byte[]> export(@RequestBody AdminExportRequest req) throws UnsupportedEncodingException {
        String fmt = (req.getFormat() == null || req.getFormat().isEmpty()) ? "csv" : req.getFormat().toLowerCase();

        String ts = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String fileName = "证书导出-" + ts + "." + ("xlsx".equals(fmt) ? "xlsx" : "csv");

        byte[] data;
        String contentType;

        if ("xlsx".equals(fmt)) {
            // 如需启用 XLSX，请在 AdminExportService 实现 exportXlsx 再放开这里：
            // data = exportSvc.exportXlsx(req.getColumns(), req.getLimit(), req.getQ(), req.getFromDate(), req.getToDate());
            // contentType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
            // 暂时回退到 CSV，避免引入依赖
            data = exportSvc.exportCsv(req.getColumns(), req.getLimit(), req.getQ(), req.getFromDate(), req.getToDate());
            contentType = "text/csv; charset=UTF-8";
        } else {
            data = exportSvc.exportCsv(req.getColumns(), req.getLimit(), req.getQ(), req.getFromDate(), req.getToDate());
            contentType = "text/csv; charset=UTF-8";
        }

        String encoded = URLEncoder.encode(fileName, String.valueOf(StandardCharsets.UTF_8)).replaceAll("\\+", "%20");
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + encoded)
                .header(HttpHeaders.CONTENT_TYPE, contentType)
                .body(data);
    }

}
