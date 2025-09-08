package org.zhejianglab.dxjh.modules.admin.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.zhejianglab.dxjh.modules.admin.entity.AdmRole;

import java.util.Optional;

/**
 * @author :og-twelve
 * @date : 2025/9/8
 */
public interface AdmRoleRepository extends JpaRepository<AdmRole, Long> {
    Optional<AdmRole> findByCode(String code);
}
