package org.zhejianglab.anniversary.modules.annivquiz.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import org.zhejianglab.anniversary.common.entity.BaseEntity;

/**
 * @author :og-twelve
 * @date : 2025/9/1
 */
@Entity
@Table(name = "anniv_quiz_option")
@Getter
@Setter
public class AnnivQuizOption extends BaseEntity {

    /** 所属题目 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    AnnivQuizQuestion question;

    /** 选项序号（1/2/3/4 或映射到 A/B/C/D） */
    @Column(name = "idx_no", nullable = false)
    Integer idxNo;

    /** 选项内容 */
    @Column(nullable = false, length = 255)
    String content;

    /** 是否正确答案（后端判题用；对前端不返回） */
    @Column(name = "is_correct", nullable = false)
    Boolean isCorrect = false;
}