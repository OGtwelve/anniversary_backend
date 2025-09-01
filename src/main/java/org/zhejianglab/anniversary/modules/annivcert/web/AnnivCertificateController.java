package org.zhejianglab.anniversary.modules.annivcert.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.zhejianglab.anniversary.common.response.ErrorResponse;
import org.zhejianglab.anniversary.common.response.SuccessResponse;
import org.zhejianglab.anniversary.modules.annivcert.dto.*;
import org.zhejianglab.anniversary.modules.annivcert.service.AnnivCertificateService;

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
                http.getRemoteAddr(), http.getHeader("User-Agent"), req.getPassToken());

        // 从 ResponseEntity 中提取 CertificateDto
        if (response.getStatusCode() == HttpStatus.OK) {
            CertificateDto certificate = (CertificateDto) response.getBody();

            // 成功时，返回 200 OK 和生成的证书数据
            SuccessResponse successResponse = new SuccessResponse("保存成功", certificate);
            return ResponseEntity.status(HttpStatus.OK).body(successResponse);  // 200 OK 和数据 + 成功消息
        } else {
            // 如果服务层返回的是错误，直接返回服务层的错误信息
            return response;
        }
    }

}
