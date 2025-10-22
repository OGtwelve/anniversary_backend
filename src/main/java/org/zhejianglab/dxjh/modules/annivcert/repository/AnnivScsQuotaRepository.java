package org.zhejianglab.dxjh.modules.annivcert.repository;


import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import javax.persistence.LockModeType;

import org.springframework.data.repository.query.Param;
import org.zhejianglab.dxjh.modules.annivcert.entity.AnnivScsQuota;
import org.zhejianglab.dxjh.modules.annivcert.entity.AnnivScsQuotaId;

import java.util.List;

/**
 * @author :og-twelve
 * @date : 2025/8/30
 */
public interface AnnivScsQuotaRepository extends JpaRepository<AnnivScsQuota, AnnivScsQuotaId> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    AnnivScsQuota findTopByIssuedLessThanOrderByIssuedAscScsCodeAsc(Integer limitCnt);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<AnnivScsQuota> findAllByOrderByScsCodeAsc();

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    AnnivScsQuota findTopByVersionCodeAndIssuedLessThanOrderByIssuedAscScsCodeAsc(
            String versionCode, Integer issuedUpperBound);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<AnnivScsQuota> findAllByVersionCodeOrderByScsCodeAsc(String versionCode);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select q from AnnivScsQuota q " +
            "where q.versionCode = :version and q.issued < q.limitCnt " +
            "order by q.issued asc, q.scsCode asc")
    List<AnnivScsQuota> findAvailableForVersion(@Param("version") String version,
                                                Pageable pageable);

    boolean existsByVersionCodeAndScsCode(String versionCode, String scsCode);

    @Modifying
    @Query("update AnnivScsQuota q set q.versionCode = :ver where q.versionCode is null")
    void patchNullVersionTo(@Param("ver") String ver);

    int countByVersionCode(String versionCode);
}
