package org.zhejianglab.dxjh.modules.annivcert.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

/**
 * @author :og-twelve
 * @date : 2025/10/22
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CertificateOpenDayDto {

    String fullNo;
    String scsCode;
    String name;
    LocalDate dateOfBirth;
    String wishes;

}