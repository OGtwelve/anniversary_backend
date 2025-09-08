package org.zhejianglab.dxjh.modules.annivcert.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.zhejianglab.dxjh.modules.annivcert.entity.AnnivCertificate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author :og-twelve
 * @date : 2025/8/30
 */
public interface AnnivCertificateRepository extends JpaRepository<AnnivCertificate, Long> {

    // 根据姓名和工号查找已存在的证书记录
    AnnivCertificate findByNameAndWorkNo(String name, String workNo);

    long count();

    long countByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

    // 平均“工龄天数”：按 2025-09-06 目标日与 start_date 的天数差
    @Query(value = "select avg(datediff(:target, start_date)) from anniv_certificate", nativeQuery = true)
    Double avgWorkDays(@Param("target") LocalDate target);

    // 有效祝福（非空且去空格后长度>0）
    @Query(value = "select count(*) from anniv_certificate where wishes is not null and length(trim(wishes))>0", nativeQuery = true)
    long countValidWishes();

    List<AnnivCertificate> findAllByOrderByCreatedAtDesc(Pageable pageable);

    @Query(value = "select date(c.created_at) as d, count(*) as cnt from anniv_certificate c where c.created_at >= :start and c.created_at < :end group by date(c.created_at) order by d", nativeQuery = true)
    List<Object[]> countDaily(@Param("start") LocalDateTime start,
                              @Param("end") LocalDateTime end);

}
