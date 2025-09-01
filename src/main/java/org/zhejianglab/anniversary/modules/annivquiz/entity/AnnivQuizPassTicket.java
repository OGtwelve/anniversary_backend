package org.zhejianglab.anniversary.modules.annivquiz.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import org.zhejianglab.anniversary.common.entity.BaseEntity;
import java.time.LocalDateTime;

/**
 * @author :og-twelve
 * @date : 2025/9/1
 */
@Entity
@Table(
        name = "anniv_quiz_pass_ticket",
        indexes = {
                @Index(name = "idx_expire", columnList = "expires_at"),
                @Index(name = "idx_used", columnList = "used_for_cert")
        }
)
@Getter
@Setter
public class AnnivQuizPassTicket extends BaseEntity {

    /** 关联问卷 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_id", nullable = false)
    AnnivQuiz quiz;

    /** 一次性通行令牌（全对后生成），例如去横线 UUID */
    @Column(nullable = false, length = 64, unique = true)
    String token;

    /** 令牌过期时间（如创建后 10 分钟） */
    @Column(name = "expires_at", nullable = false)
    LocalDateTime expiresAt;

    /** 提交端信息（可选校验） */
    @Column(length = 45)
    String ip;

    @Column(length = 255)
    String ua;

    /** 是否已用于签发证书（防重复使用） */
    @Column(name = "used_for_cert", nullable = false)
    Boolean usedForCert = false;

    /** 本次答案快照（questionId -> optionId 的 JSON 字符串） */
    @Column(name = "answer_json", columnDefinition = "json")
    String answerJson;

    /** 答对题数（审计/统计） */
    @Column(name = "correct_count", nullable = false)
    Integer correctCount = 0;

    /** 是否全部正确 */
    @Column(name = "all_correct", nullable = false)
    Boolean allCorrect = false;
}