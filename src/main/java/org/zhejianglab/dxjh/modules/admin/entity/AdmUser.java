package org.zhejianglab.dxjh.modules.admin.entity;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;
import lombok.Getter; import lombok.Setter;
import org.zhejianglab.dxjh.common.entity.BaseEntity;

/**
 * @author :og-twelve
 * @date : 2025/9/8
 */
@Entity @Table(name="admin_user", indexes = {
        @Index(name="idx_user_username", columnList="username", unique=true)
})
@Getter @Setter
public class AdmUser extends BaseEntity {
    @Column(nullable=false, length=64, unique=true)
    String username;
    @Column(nullable=false, length=128)
    String displayName;
    @Column(nullable=false, length=200)
    String passwordHash;
    @Column(name = "is_enabled", nullable=false)
    Boolean isEnabled = true;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name="admin_user_role",
            joinColumns = @JoinColumn(name="user_id"),
            inverseJoinColumns = @JoinColumn(name="role_id"))
    Set<AdmRole> roles = new HashSet<>();
}