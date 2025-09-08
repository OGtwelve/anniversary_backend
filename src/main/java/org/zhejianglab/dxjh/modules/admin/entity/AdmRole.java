package org.zhejianglab.dxjh.modules.admin.entity;

import javax.persistence.*;

import lombok.Getter; import lombok.Setter;
import org.zhejianglab.dxjh.common.entity.BaseEntity;

/**
 * @author :og-twelve
 * @date : 2025/9/8
 */
@Entity @Table(name="admin_role", uniqueConstraints = @UniqueConstraint(columnNames="code"))
@Getter @Setter
public class AdmRole extends BaseEntity {
    @Column(nullable=false, length=64)
    String code;   // e.g. ADMIN, USER
    @Column(nullable=false, length=128)
    String name;
}
