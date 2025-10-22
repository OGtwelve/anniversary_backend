package org.zhejianglab.dxjh.modules.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author :og-twelve
 * @date : 2025/10/22
 */
@Data
@AllArgsConstructor
public class AdminCertificateRowDto_OpenDay {
    String id;          // 用证书号 fullNo
    String name;
    String joinDate;    // yyyy/MM/dd
    String blessing;
    String createdAt;   // yyyy/MM/dd HH:mm:ss
}