package org.zhejianglab.dxjh.modules.annivgate.web;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.zhejianglab.dxjh.modules.annivgate.service.AnnivGateService;

import java.util.HashMap;
import java.util.Map;

/**
 * @author :og-twelve
 * @date : 2025/9/4
 */
@RestController
@RequestMapping("/api/anniv/gate")
@RequiredArgsConstructor
public class AnnivGateController {

    private final AnnivGateService gateService;

    @GetMapping("/check")
    public ResponseEntity<Object> check() {
        AnnivGateService.CheckResult r = gateService.checkOpen();

        Map<String, Object> body = new HashMap<>();
        body.put("message", r.message);
        body.put("data", r.allowed);           // 你要的 true/false
        body.put("serverNow", r.serverNow);
        body.put("openAt", r.openAt);
        body.put("closeAt", r.closeAt);

        return ResponseEntity.ok(body);
    }
}
