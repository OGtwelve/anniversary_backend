package org.zhejianglab.anniversary.modules.annivcert.entity;


import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

/**
 * @author :og-twelve
 * @date : 2025/8/30
 */
@Entity
@Table(name = "anniv_seq_counter")
@Setter
@Getter
public class AnnivSeqCounter {
    @Id
    @Column(length=16)
    String name;           // 固定 "ANNIV_CERT"

    @Column(name="last_seq", nullable=false)
    Integer lastSeq;
}
