package org.phong.horizon.user.subdomain.role.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.phong.horizon.core.superclass.BaseEntity;
import org.phong.horizon.user.subdomain.permission.entities.Permission;

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

