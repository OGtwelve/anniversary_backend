package org.zhejianglab.anniversary.modules.annivcert.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import javax.persistence.LockModeType;
import java.util.Optional;
import org.zhejianglab.anniversary.modules.annivcert.entity.AnnivSeqCounter;

/**
 * @author :og-twelve
 * @date : 2025/8/30
 */
public interface AnnivSeqCounterRepository extends JpaRepository<AnnivSeqCounter, String> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select s from AnnivSeqCounter s where s.name = :name")
    Optional<AnnivSeqCounter> lockByName(@Param("name") String name);

}
