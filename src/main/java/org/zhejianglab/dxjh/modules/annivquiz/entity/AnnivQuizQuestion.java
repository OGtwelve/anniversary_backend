package org.zhejianglab.dxjh.modules.annivquiz.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import org.zhejianglab.dxjh.common.entity.BaseEntity;

/**
 * @author :og-twelve
 * @date : 2025/9/1
 */

@Entity
@Table(name = "anniv_quiz_question")
@Getter
@Setter
public class AnnivQuizQuestion extends BaseEntity {

    /** 所属问卷 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_id", nullable = false)
    private AnnivQuiz quiz;

    /** 展示顺序（从 1 开始） */
    @Column(name = "idx_no", nullable = false)
    private Integer idxNo;

    /** 题干 */
    @Column(nullable = false, length = 255)
    private String content;
}
