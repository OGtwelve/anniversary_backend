package org.zhejianglab.dxjh.modules.annivquiz.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.zhejianglab.dxjh.modules.annivquiz.entity.AnnivQuizQuestion;

import java.util.List;

/**
 * @author :og-twelve
 * @date : 2025/9/1
 */
public interface AnnivQuizQuestionRepository extends JpaRepository<AnnivQuizQuestion, Long> {
    List<AnnivQuizQuestion> findByQuizIdOrderByIdxNoAsc(Long quizId);
}
