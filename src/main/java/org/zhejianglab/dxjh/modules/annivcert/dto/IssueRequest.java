package org.zhejianglab.dxjh.modules.annivcert.dto;

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

    @NotBlank(message = "姓名不能为空")
    private String name;

    @NotNull(message = "入职日期不能为空")
    private LocalDate startDate;

    @NotBlank(message = "工号不能为空")
    private String workNo;

    @NotBlank(message = "请先完成问卷并携带 passToken")
    private String passToken;

    @NotBlank(message = "请先填写祝福语再来吧")
    private String wishes; // 祝福语

}
