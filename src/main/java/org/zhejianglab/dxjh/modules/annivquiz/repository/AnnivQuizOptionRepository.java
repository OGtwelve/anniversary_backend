package org.zhejianglab.dxjh.modules.annivquiz.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.zhejianglab.dxjh.modules.annivquiz.entity.AnnivQuizOption;

import java.util.Collection;
import java.util.List;

/**
 * @author :og-twelve
 * @date : 2025/9/1
 */
public interface AnnivQuizOptionRepository extends JpaRepository<AnnivQuizOption, Long> {
    List<AnnivQuizOption> findByQuestionIdOrderByIdxNoAsc(Long questionId);

    // 批量取出每道题的“正确选项”（注意把 o.correct 改成你实体里的布尔字段名：isCorrect / answer / right 等）
    @Query("select o.question.id, o.id from AnnivQuizOption o where o.question.id in :qids and o.isCorrect = true")
    List<Object[]> findCorrectOptionPairs(@Param("qids") Collection<Long> questionIds);
}
