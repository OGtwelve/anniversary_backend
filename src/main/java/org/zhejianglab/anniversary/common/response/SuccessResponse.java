package org.zhejianglab.anniversary.common.response;

import lombok.*;
import org.zhejianglab.anniversary.modules.annivcert.dto.CertificateDto;

/**
 * @author :og-twelve
 * @date : 2025/8/30
 */
@Setter
@Getter
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SuccessResponse {
    private String message;
    private CertificateDto certificate;
}

