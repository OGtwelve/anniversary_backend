package org.zhejianglab.dxjh.modules.annivcert.repository;


import org.springframework.data.jpa.repository.*;
import javax.persistence.LockModeType;
import org.zhejianglab.dxjh.modules.annivcert.entity.AnnivScsQuota;

import java.util.List;

/**
 * @author :og-twelve
 * @date : 2025/8/30
 */
public interface AnnivScsQuotaRepository extends JpaRepository<AnnivScsQuota, String> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    AnnivScsQuota findTopByIssuedLessThanOrderByIssuedAscScsCodeAsc(Integer limitCnt);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<AnnivScsQuota> findAllByOrderByScsCodeAsc();
}
