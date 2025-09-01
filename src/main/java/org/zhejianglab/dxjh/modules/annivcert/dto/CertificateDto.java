package org.zhejianglab.dxjh.modules.annivcert.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

/**
 * @author :og-twelve
 * @date : 2025/8/30
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CertificateDto {

    String fullNo;
    String scsCode;
    Integer daysToTarget;
    String name;
    LocalDate startDate;
    String workNo;

}
