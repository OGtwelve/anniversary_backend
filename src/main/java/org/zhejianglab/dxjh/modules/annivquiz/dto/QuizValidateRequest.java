package org.zhejianglab.dxjh.modules.annivquiz.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author :og-twelve
 * @date : 2025/9/1
 */

@Getter 
@Setter
public class QuizValidateRequest {
    @NotBlank
    String quizCode;

    @NotNull
    List<Answer> answers; // 每题选择

    @Getter 
    @Setter
    public static class Answer {
        @NotNull 
        Long questionId;
        @NotNull 
        Long optionId;
    }
}
