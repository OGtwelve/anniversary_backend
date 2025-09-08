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
public class SurveyStatsDto {
    long totalParticipants;
    long passedParticipants;
    int passRate;         // 0~100
    double averageScore;  // 平均正确题数（保留1位小数）
    long todayAnswers;
    List<QuestionStatsDto> questions = new ArrayList<>();
}
