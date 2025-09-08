package org.zhejianglab.dxjh.modules.admin.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.Set;

/**
 * @author :og-twelve
 * @date : 2025/9/8
 */
@Data
public class CreateUserRequest {
    @NotBlank
    String username;
    @NotBlank
    String displayName;
    @NotBlank
    String password;
    Set<String> roleCodes; // e.g. ["ADMIN","USER"]
}
