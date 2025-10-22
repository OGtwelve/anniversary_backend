package org.zhejianglab.dxjh.modules.annivcert.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * @author :og-twelve
 * @date : 2025/10/22
 */
@Setter
@Getter
public class IssueRequestOpenDay {

    @NotBlank(message = "姓名不能为空")
    private String name;

    @NotNull(message = "出生日期不能为空")
    private LocalDate dateOfBirth;

    private String passToken;

    @NotBlank(message = "请先填写祝福语再来吧")
    private String wishes; // 祝福语

}
