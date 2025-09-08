package org.zhejianglab.dxjh.modules.admin.dto;

import lombok.Data;

import java.util.Set;

/**
 * @author :og-twelve
 * @date : 2025/9/8
 */
@Data
public class LoginResponse {
    String token;
    String username;
    String displayName;
    Set<String> roles;
    public LoginResponse(String token, String username, String displayName, Set<String> roles){
        this.token = token; this.username = username; this.displayName = displayName; this.roles = roles;
    }
}

