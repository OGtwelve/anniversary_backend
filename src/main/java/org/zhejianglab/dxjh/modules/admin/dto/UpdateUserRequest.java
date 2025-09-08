package org.zhejianglab.dxjh.modules.admin.dto;

import lombok.Data;

import java.util.Set;

/**
 * @author :og-twelve
 * @date : 2025/9/8
 */
@Data
public class UpdateUserRequest {
    String displayName;
    Boolean enabled;
    Set<String> roleCodes;
}
