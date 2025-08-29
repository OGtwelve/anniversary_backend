package org.zhejianglab.anniversary.modules.annivcert.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * @author :og-twelve
 * @date : 2025/8/30
 */
@Setter
@Getter
public class IssueRequest {


    @NotBlank
    String name;

    @NotNull
    LocalDate startDate;

    @NotNull
    String workNo;


}
