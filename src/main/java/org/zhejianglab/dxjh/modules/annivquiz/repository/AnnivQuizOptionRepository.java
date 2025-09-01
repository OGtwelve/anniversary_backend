package org.zhejianglab.dxjh.modules.annivquiz.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.zhejianglab.dxjh.modules.annivquiz.entity.AnnivQuizOption;

import java.util.List;

/**
 * @author :og-twelve
 * @date : 2025/9/1
 */
public interface AnnivQuizOptionRepository extends JpaRepository<AnnivQuizOption, Long> {
    List<AnnivQuizOption> findByQuestionIdOrderByIdxNoAsc(Long questionId);
}
