package org.zhejianglab.dxjh.modules.annivcert.entity;


import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;


/**
 * @author :og-twelve
 * @date : 2025/8/30
 */
@Entity
@Table(name = "anniv_quota")
@Setter
@Getter
public class AnnivScsQuota {

    @Id
    @Column(name="scs_code", length=6)
    String scsCode;          // SCS01..SCS12

    @Column(nullable=false)
    Integer issued = 0;

    @Column(name="limit_cnt", nullable=false)
    Integer limitCnt = 125;

}
