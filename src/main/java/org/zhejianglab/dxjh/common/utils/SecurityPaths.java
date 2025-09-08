package org.zhejianglab.dxjh.common.utils;

/**
 * @author :og-twelve
 * @date : 2025/9/8
 */
public final class SecurityPaths {
    private SecurityPaths() {}
    public static final String[] PUBLIC = {
            "/api/auth/login",
            "/api/auth/check-open",
            "/api/anniv/**",          // 你允许匿名访问的业务端点
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/swagger-resources/**",
            "/swagger-ui.html",
            "/actuator/**",
            "/", "/index.html", "/assets/**", "/favicon.ico"
    };
}
