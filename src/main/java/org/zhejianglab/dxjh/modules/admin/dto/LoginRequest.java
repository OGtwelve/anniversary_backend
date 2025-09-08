package org.zhejianglab.dxjh.modules.admin.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author :og-twelve
 * @date : 2025/9/8
 */
@Data
public class LoginRequest {
    @NotBlank
    String username;
    @NotBlank
    String password;
}
