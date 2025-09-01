package org.zhejianglab.anniversary.modules.annivquiz.dto;


import lombok.AllArgsConstructor;
import lombok.Getter; import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author :og-twelve
 * @date : 2025/9/1
 */
@Getter @Setter @AllArgsConstructor
public class QuizValidateResultDto {
    boolean allCorrect;
    List<Item> items;           // 每题对错
    String passToken;           // 全对才返回
    LocalDateTime expiresAt;    // 全对才返回

    @Getter @Setter @AllArgsConstructor
    public static class Item {
        Long questionId;
        boolean correct;
    }
}