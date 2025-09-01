package org.zhejianglab.dxjh.modules.annivquiz.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author :og-twelve
 * @date : 2025/9/1
 */
@Getter 
@Setter
public class QuizDto {
    String quizCode;
    String title;
    List<Question> questions;

    @Getter 
    @Setter
    public static class Question {
        Long id;
        Integer idxNo;
        String content;
        List<Option> options;
    }

    @Getter 
    @Setter
    public static class Option {
        Long id;
        Integer idxNo;
        String content;
    }
}
