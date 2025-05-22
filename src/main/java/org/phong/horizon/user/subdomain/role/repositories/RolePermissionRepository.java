package org.phong.horizon.user.subdomain.role.repositories;

import org.phong.horizon.user.subdomain.permission.entities.Permission;
import org.phong.horizon.user.subdomain.role.entities.Role;
import org.phong.horizon.user.subdomain.role.entities.RolePermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Repository
public interface RolePermissionRepository extends JpaRepository<RolePermission, UUID> {

    Optional<RolePermission> findByRoleAndPermission(Role role, Permission permission);

    List<RolePermission> findByRole(Role role);

    List<RolePermission> findByRoleId(UUID roleId);

    @Modifying
    @Query("DELETE FROM RolePermission rp WHERE rp.role = :role AND rp.permission = :permission")
    void deleteByRoleAndPermission(@Param("role") Role role, @Param("permission") Permission permission);

    @Modifying
    @Query("DELETE FROM RolePermission rp WHERE rp.role.id = :roleId")
    void deleteByRoleId(@Param("roleId") UUID roleId);

    @Modifying
    @Query("DELETE FROM RolePermission rp WHERE rp.role.id = :roleId AND rp.permission.id NOT IN :permissionIds")
    void deleteByRoleIdAndPermissionIdNotIn(@Param("roleId") UUID roleId, @Param("permissionIds") Set<UUID> permissionIds);

    boolean existsByRoleIdAndPermissionId(UUID roleId, UUID permissionId);
}

