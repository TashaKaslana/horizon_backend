package org.phong.horizon.user.subdomain.role.repositories;

import org.phong.horizon.user.subdomain.role.entities.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RoleRepository extends JpaRepository<Role, UUID>, JpaSpecificationExecutor<Role> {
    Optional<Role> findBySlug(String slug);
    boolean existsByName(String name);
    boolean existsBySlug(String slug);
}

