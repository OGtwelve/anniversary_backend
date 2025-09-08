package org.zhejianglab.dxjh.common.utils;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author :og-twelve
 * @date : 2025/9/8
 */
@Data
@ConfigurationProperties(prefix = "security.jwt")
public class JwtProps {
    private String secret;
    private long expireMinutes;
}
