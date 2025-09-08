package org.zhejianglab.dxjh.modules.annivquiz.dto;


import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author :og-twelve
 * @date : 2025/9/8
 */
@Data
@Getter
@Setter
public class QuestionStatsDto {
    Long id;
    String question;
    long totalAnswers;
    long correctAnswers;
    int correctRate;   // 0~100
    boolean isSimple;  // 这里用“正确率>=80%”作为简单题的判定

    public QuestionStatsDto() {}

    public QuestionStatsDto(Long id, String question) {
        this.id = id;
        this.question = question;
    }
}
