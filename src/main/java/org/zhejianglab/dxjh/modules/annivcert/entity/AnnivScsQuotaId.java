package org.zhejianglab.dxjh.modules.annivcert.entity;

/**
 * @author :og-twelve
 * @date : 2025/10/22
 */
// 复合键
import java.io.Serializable;
import lombok.*;
@Data @NoArgsConstructor @AllArgsConstructor
public class AnnivScsQuotaId implements Serializable {
    private String versionCode;
    private String scsCode;
}

