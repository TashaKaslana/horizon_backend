package org.phong.horizon.user.infrastructure.persistence.entities.role;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.phong.horizon.core.superclass.BaseEntity;

@Getter
@Setter
@Entity
@Table(name = "roles_permissions", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"role_id", "permission_id"})
})
public class RolePermission extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "permission_id", nullable = false)
    private Permission permission;
}

