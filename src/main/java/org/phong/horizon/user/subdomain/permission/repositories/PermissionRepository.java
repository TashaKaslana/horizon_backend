package org.phong.horizon.user.subdomain.permission.repositories;

import org.phong.horizon.user.subdomain.permission.entities.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, UUID>, JpaSpecificationExecutor<Permission> {
    Optional<Permission> findBySlug(String slug);
    boolean existsByName(String name);
    boolean existsBySlug(String slug);
}

