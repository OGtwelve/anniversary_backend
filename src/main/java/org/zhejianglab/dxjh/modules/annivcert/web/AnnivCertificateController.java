package org.zhejianglab.dxjh.modules.annivcert.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import org.apache.poi.xssf.usermodel.*;
import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.zhejianglab.dxjh.common.response.SuccessResponse;
import org.zhejianglab.dxjh.modules.annivcert.dto.*;
import org.zhejianglab.dxjh.modules.annivcert.entity.AnnivCertificate;
import org.zhejianglab.dxjh.modules.annivcert.service.AnnivCertificateService;

/**
 * @author :og-twelve
 * @date : 2025/8/30
 */
@RestController
@RequestMapping("/api/anniv/certificates")
public class AnnivCertificateController {
    final AnnivCertificateService svc;
    public AnnivCertificateController(AnnivCertificateService svc){ this.svc = svc; }

    @PostMapping("/issue")
    public ResponseEntity<Object> issue(@RequestBody @Valid IssueRequest req, HttpServletRequest http) {
        // 调用服务层方法来处理证书生成逻辑，返回的是 ResponseEntity<Object>
        ResponseEntity<Object> response = svc.issue(req.getName(), req.getStartDate(), req.getWorkNo(),
                http.getRemoteAddr(), http.getHeader("User-Agent"), req.getPassToken(), req.getWishes());

        CertificateDto certificate = (CertificateDto) response.getBody();

        // 成功时，返回 200 OK 和生成的证书数据
        SuccessResponse successResponse = new SuccessResponse("保存成功", certificate);
        return ResponseEntity.status(HttpStatus.OK).body(successResponse);  // 200 OK 和数据 + 成功消息
    }

    @GetMapping("/downloadExcelData")
    public void downloadExcelData(HttpServletResponse response) throws Exception {
        // 从服务层取要导出的数据（你可以加筛选参数）
        List<AnnivCertificate> list = svc.findAllForExport();

        XSSFWorkbook wb = new XSSFWorkbook();
        XSSFSheet sheet = wb.createSheet("certificates");

        int r = 0;
        XSSFRow header = sheet.createRow(r++);
        header.createCell(0).setCellValue("id");
        header.createCell(1).setCellValue("证书号");
        header.createCell(2).setCellValue("卫星代码");
        header.createCell(3).setCellValue("顺位排名");
        header.createCell(4).setCellValue("距离2025-09-06天数");
        header.createCell(5).setCellValue("姓名");
        header.createCell(6).setCellValue("入职日期");
        header.createCell(7).setCellValue("工号");
        header.createCell(8).setCellValue("祝福语");
        header.createCell(9).setCellValue("创建时间");

        for (AnnivCertificate c : list) {
            XSSFRow row = sheet.createRow(r++);
            row.createCell(0).setCellValue(c.getId());
            row.createCell(1).setCellValue(c.getFullNo());
            row.createCell(2).setCellValue(c.getScsCode());
            row.createCell(3).setCellValue(c.getSeq());
            row.createCell(4).setCellValue(c.getDaysToTarget());
            row.createCell(5).setCellValue(c.getName());
            row.createCell(6).setCellValue(c.getStartDate() == null ? "" : c.getStartDate().toString());
            row.createCell(7).setCellValue(c.getWorkNo());
            row.createCell(8).setCellValue(c.getWishes());
            row.createCell(9).setCellValue(c.getCreatedAt() == null ? "" : c.getCreatedAt().toString());
        }
        for (int i = 0; i <= 9; i++) sheet.autoSizeColumn(i);

        String fileName = URLEncoder.encode("最新证书数据.xlsx", StandardCharsets.UTF_8.name());
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");

        wb.write(response.getOutputStream());
        wb.close();
    }


}
