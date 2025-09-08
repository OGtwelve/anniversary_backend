package org.zhejianglab.dxjh.modules.admin.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.zhejianglab.dxjh.modules.admin.entity.AdmUser;

import java.util.Optional;

/**
 * @author :og-twelve
 * @date : 2025/9/8
 */
public interface AdmUserRepository extends JpaRepository<AdmUser, Long> {
    // 带角色一起抓
    @EntityGraph(attributePaths = "roles")
    Optional<AdmUser> findByUsername(String username);
    boolean existsByUsername(String username);
}
