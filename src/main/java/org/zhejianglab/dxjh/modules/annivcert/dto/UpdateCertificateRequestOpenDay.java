package org.zhejianglab.dxjh.modules.annivcert.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;

/**
 * @author :og-twelve
 * @date : 2025/10/22
 */
@Data
public class UpdateCertificateRequestOpenDay {
    String name;       // 前端 name -> 后端 name
    @JsonFormat(pattern = "yyyy-MM-dd")
    LocalDate joinDate; // 前端 joinDate -> 后端 dateOfBirth
    String blessing;    // 前端 blessing -> 后端 wishes
}
