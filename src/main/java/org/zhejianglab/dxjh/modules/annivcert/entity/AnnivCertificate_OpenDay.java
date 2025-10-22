package org.zhejianglab.dxjh.modules.annivcert.entity;

import lombok.Getter;
import lombok.Setter;
import org.zhejianglab.dxjh.common.entity.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.time.LocalDate;

/**
 * @author :og-twelve
 * @date : 2025/10/22
 */
@Entity
@Table(name = "anniv_certificate_open_day",
        uniqueConstraints = @UniqueConstraint(name="uk_full_no", columnNames = "full_no"))
@Getter
@Setter
public class AnnivCertificate_OpenDay extends BaseEntity {

    @Column(name="full_no", nullable=false, unique=true, length=32)
    String fullNo;              // SCS01-0880-0001

    @Column(name="scs_code", nullable=false, length=6)
    String scsCode;             // SCS01..SCS12

    @Column(nullable=false)
    Integer seq;                // 1..1500

    @Column(nullable=false, length=64)
    String name;

    @Column(name="date_of_birth", nullable=false)
    LocalDate dateOfBirth;

    @Column(name = "wishes", columnDefinition = "LONGTEXT")
    String wishes;

    @Column(name = "ip", columnDefinition = "LONGTEXT")
    String ip;
    @Column(name = "ua", columnDefinition = "LONGTEXT")
    String ua;

}

