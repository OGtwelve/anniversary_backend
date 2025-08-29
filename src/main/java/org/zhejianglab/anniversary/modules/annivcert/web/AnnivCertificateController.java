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
        try {
            // 调用服务层方法来处理证书生成逻辑
            CertificateDto certificate = svc.issue(req.getName(), req.getStartDate(), req.getWorkNo(),
                    http.getRemoteAddr(), http.getHeader("User-Agent"));

            // 成功时，返回 200 OK 和生成的证书数据
            return ResponseEntity.status(HttpStatus.OK).body(new SuccessResponse("保存成功", certificate));  // 200 OK 和数据 + 成功消息
        } catch (IllegalStateException ex) {
            // 处理业务异常（例如名额已满）
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("Error: " + ex.getMessage()));  // 400 Bad Request
        } catch (Exception ex) {
            // 捕获其他异常并返回 500 错误
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Unexpected error occurred: " + ex.getMessage()));  // 500 Internal Server Error
        }
    }

}
