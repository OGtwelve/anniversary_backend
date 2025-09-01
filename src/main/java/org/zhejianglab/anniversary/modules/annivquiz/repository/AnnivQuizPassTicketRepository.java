package org.zhejianglab.anniversary.modules.annivquiz.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.zhejianglab.anniversary.modules.annivquiz.entity.AnnivQuizPassTicket;

import java.util.Optional;

/**
 * @author :og-twelve
 * @date : 2025/9/1
 */
public interface AnnivQuizPassTicketRepository extends JpaRepository<AnnivQuizPassTicket, Long> {
    Optional<AnnivQuizPassTicket> findByToken(String token);
}
