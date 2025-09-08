package org.zhejianglab.dxjh.modules.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author :og-twelve
 * @date : 2025/9/8
 */
@Data
@AllArgsConstructor
public class AdminCertificateRowDto {
    String id;          // 用证书号 fullNo
    String name;
    String employeeId;  // 工号
    String joinDate;    // yyyy/MM/dd
    int    workYears;   // 实际是“工龄天数”，前端字段名沿用
    String blessing;
    String createdAt;   // yyyy/MM/dd HH:mm:ss
}
