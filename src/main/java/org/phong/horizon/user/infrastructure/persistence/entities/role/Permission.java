package org.phong.horizon.user.infrastructure.persistence.entities.role;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.phong.horizon.core.superclass.BaseEntity;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "permissions")
public class Permission extends BaseEntity {
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "slug", nullable = false, unique = true)
    private String slug;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "module", nullable = false)
    private String module;

    @OneToMany(mappedBy = "permission")
    private Set<RolePermission> rolePermissions;
}

