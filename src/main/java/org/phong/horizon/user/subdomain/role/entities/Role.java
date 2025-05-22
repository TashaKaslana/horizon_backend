package org.phong.horizon.user.subdomain.role.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.phong.horizon.core.superclass.BaseEntity;

import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "roles")
public class Role extends BaseEntity {
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "slug", nullable = false, unique = true)
    private String slug;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @OneToMany(mappedBy = "role")
    private Set<RolePermission> rolePermissions;
}

