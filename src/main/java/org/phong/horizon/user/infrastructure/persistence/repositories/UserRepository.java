package org.phong.horizon.user.infrastructure.persistence.repositories;

import org.phong.horizon.user.infrastructure.persistence.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByAuth0Id(String auth0Id);
}