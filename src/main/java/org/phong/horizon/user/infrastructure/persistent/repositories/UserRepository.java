package org.phong.horizon.user.infrastructure.persistent.repositories;

import org.phong.horizon.user.infrastructure.persistent.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
}