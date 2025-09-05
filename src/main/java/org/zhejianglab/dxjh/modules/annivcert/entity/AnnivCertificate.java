package org.zhejianglab.dxjh.modules.annivcert.entity;

import lombok.Getter;
import lombok.Setter;
import org.zhejianglab.dxjh.common.entity.BaseEntity;
import javax.persistence.*;
import java.time.LocalDate;

/**
 * @author :og-twelve
 * @date : 2025/8/30
 */
@Entity
@Table(name = "anniv_certificate",
        uniqueConstraints = @UniqueConstraint(name="uk_full_no", columnNames = "full_no"))
@Getter
@Setter
public class AnnivCertificate extends BaseEntity {

    @Column(name="full_no", nullable=false, unique=true, length=32)
    String fullNo;              // SCS01-0880-0001

    @Column(name="scs_code", nullable=false, length=6)
    String scsCode;             // SCS01..SCS12

    @Column(nullable=false)
    Integer seq;                // 1..1500

    @Column(name="days_to_target", nullable=false)
    Integer daysToTarget;       // 入职至 2025-09-06 的天数

    @Column(nullable=false, length=64)
    String name;

    @Column(name="start_date", nullable=false)
    LocalDate startDate;

    @Column(name="work_no", nullable=false)
    String workNo;

    @Column(name = "wishes", columnDefinition = "LONGTEXT")
    String wishes;

    String ip;
    String ua;

}
