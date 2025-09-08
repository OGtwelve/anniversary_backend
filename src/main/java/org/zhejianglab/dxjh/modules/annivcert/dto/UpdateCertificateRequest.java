package org.zhejianglab.dxjh.modules.annivcert.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;

/**
 * @author :og-twelve
 * @date : 2025/9/8
 */
@Data
public class UpdateCertificateRequest {
    String name;       // 前端 name -> 后端 name
    String employeeId; // 前端 employeeId -> 后端 workNo
    @JsonFormat(pattern = "yyyy-MM-dd")
    LocalDate joinDate; // 前端 joinDate -> 后端 startDate
    Integer workYears;  // 前端 workYears(天) -> 后端 daysToTarget（可选）
    String blessing;    // 前端 blessing -> 后端 wishes
}
