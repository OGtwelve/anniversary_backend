package org.zhejianglab.dxjh.modules.annivgate.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import org.zhejianglab.dxjh.common.entity.BaseEntity;

/**
 * @author :og-twelve
 * @date : 2025/9/4
 */
@Getter
@Setter
@Entity
@Table(name = "anniv_gate_config",
        indexes = {
                @Index(name = "idx_gate_code", columnList = "gate_code", unique = true),
                @Index(name = "idx_active_openat", columnList = "is_active, open_at")
        })
public class AnnivGateConfig extends BaseEntity {

    @Column(name = "gate_code", nullable = false, length = 64)
    private String gateCode;          // 例如：ANNIV25_GATE

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = Boolean.TRUE;

    @Column(name = "open_at", nullable = false)
    private LocalDateTime openAt;     // 开启时间（本地时区）

    @Column(name = "close_at")
    private LocalDateTime closeAt;    // 可选：截止时间（不填表示不限制）

    @Column(name = "remark", length = 255)
    private String remark;
}
