package org.zhejianglab.dxjh.modules.annivcert.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.zhejianglab.dxjh.modules.annivcert.entity.AnnivCertificate;

/**
 * @author :og-twelve
 * @date : 2025/8/30
 */
public interface AnnivCertificateRepository extends JpaRepository<AnnivCertificate, Long> {

    // 根据姓名和工号查找已存在的证书记录
    AnnivCertificate findByNameAndWorkNo(String name, String workNo);

}
