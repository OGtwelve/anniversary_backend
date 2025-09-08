package org.zhejianglab.dxjh.modules.annivquiz.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.zhejianglab.dxjh.modules.annivquiz.entity.AnnivQuizPassTicket;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * @author :og-twelve
 * @date : 2025/9/1
 */
public interface AnnivQuizPassTicketRepository extends JpaRepository<AnnivQuizPassTicket, Long> {
    Optional<AnnivQuizPassTicket> findByToken(String token);

    long countByQuizId(Long quizId);

    long countByQuizIdAndAllCorrectTrue(Long quizId);

    long countByQuizIdAndCreatedAtBetween(Long quizId, LocalDateTime start, LocalDateTime end);

    // 累计答对题数
    @Query("select coalesce(sum(t.correctCount), 0) from AnnivQuizPassTicket t where t.quiz.id = :quizId")
    Long sumCorrectCountByQuizId(@Param("quizId") Long quizId);

    List<AnnivQuizPassTicket> findByQuizId(Long quizId);  // 用于统计题目维度

}
