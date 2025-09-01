package org.zhejianglab.dxjh.modules.annivquiz.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import org.zhejianglab.dxjh.common.entity.BaseEntity;

@Entity
@Table(
        name = "anniv_quiz",
        indexes = {
                @Index(name = "uk_quiz_code", columnList = "quiz_code", unique = true)
        }
)
@Getter
@Setter
public class AnnivQuiz extends BaseEntity {

    /** 问卷编码（周年前缀），如 ANNIV25QZ-0001 */
    @Column(name = "quiz_code", nullable = false, length = 32, unique = true)
    String quizCode;

    @Column(nullable = false, length = 128)
    String title;

    /** 通过所需最少答对题数 */
    @Column(name = "pass_min_correct", nullable = false)
    Integer passMinCorrect = 1;

    /** 是否激活（前端只拉取激活问卷） */
    @Column(name = "is_active", nullable = false)
    Boolean isActive = false;
}
