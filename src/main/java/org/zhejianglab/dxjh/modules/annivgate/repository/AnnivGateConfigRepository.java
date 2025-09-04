package org.zhejianglab.dxjh.modules.annivgate.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.zhejianglab.dxjh.modules.annivgate.entity.AnnivGateConfig;

import java.util.Optional;

/**
 * @author :og-twelve
 * @date : 2025/9/4
 */
public interface AnnivGateConfigRepository extends JpaRepository<AnnivGateConfig, Long> {

    // 按 gateCode 取生效配置
    Optional<AnnivGateConfig> findByGateCodeAndIsActiveTrue(String gateCode);

    // 或者只取唯一生效项
    Optional<AnnivGateConfig> findFirstByIsActiveTrueOrderByOpenAtDesc();

}
