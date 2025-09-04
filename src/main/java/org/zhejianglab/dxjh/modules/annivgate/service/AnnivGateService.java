package org.zhejianglab.dxjh.modules.annivgate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zhejianglab.dxjh.modules.annivgate.entity.AnnivGateConfig;
import org.zhejianglab.dxjh.modules.annivgate.repository.AnnivGateConfigRepository;

import java.time.LocalDateTime;

/**
 * @author :og-twelve
 * @date : 2025/9/4
 */
@Service
@RequiredArgsConstructor
public class AnnivGateService {

    private final AnnivGateConfigRepository repo;

    // 你可以把 code 固定为 ANNIV25_GATE；或者做成可配置
    private static final String DEFAULT_CODE = "ANNIV25_GATE";

    @Transactional(readOnly = true)
    public CheckResult checkOpen() {
        AnnivGateConfig cfg = repo.findByGateCodeAndIsActiveTrue(DEFAULT_CODE)
                .orElse(null);

        LocalDateTime now = LocalDateTime.now();
        if (cfg == null) {
            return new CheckResult(false, "未配置开启时间", now, null, null);
        }

        boolean afterOpen = !now.isBefore(cfg.getOpenAt());
        boolean beforeClose = (cfg.getCloseAt() == null) || now.isBefore(cfg.getCloseAt());
        boolean allowed = afterOpen && beforeClose;

        String msg;
        if (!afterOpen) {
            msg = "活动尚未开始";
        } else if (!beforeClose) {
            msg = "活动已结束";
        } else {
            msg = "ok";
        }

        return new CheckResult(allowed, msg, now, cfg.getOpenAt(), cfg.getCloseAt());
    }

    // 简单返回体
    public static class CheckResult {
        public final boolean allowed;
        public final String message;
        public final LocalDateTime serverNow;
        public final LocalDateTime openAt;
        public final LocalDateTime closeAt;

        public CheckResult(boolean allowed, String message, LocalDateTime serverNow,
                           LocalDateTime openAt, LocalDateTime closeAt) {
            this.allowed = allowed;
            this.message = message;
            this.serverNow = serverNow;
            this.openAt = openAt;
            this.closeAt = closeAt;
        }
    }
}
