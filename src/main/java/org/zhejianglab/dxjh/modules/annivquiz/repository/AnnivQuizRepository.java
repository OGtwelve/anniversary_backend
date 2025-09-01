package org.zhejianglab.dxjh.modules.annivquiz.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.zhejianglab.dxjh.modules.annivquiz.entity.AnnivQuiz;

import java.util.Optional;

/**
 * @author :og-twelve
 * @date : 2025/9/1
 */
public interface AnnivQuizRepository extends JpaRepository<AnnivQuiz, Long> {
    Optional<AnnivQuiz> findFirstByIsActiveTrue();
    Optional<AnnivQuiz> findByQuizCode(String quizCode);
}
