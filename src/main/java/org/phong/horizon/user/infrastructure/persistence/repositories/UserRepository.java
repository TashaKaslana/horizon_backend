package org.phong.horizon.user.infrastructure.persistence.repositories;

import org.phong.horizon.user.infrastructure.persistence.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByAuth0Id(String auth0Id);

    @Query("SELECT u.id FROM User u WHERE u.auth0Id = :auth0Id")
    UUID getUserIdByAuth0Id(String auth0Id);

    @Query("SELECT u FROM User u WHERE u.username IN :usernameList")
    List<User> findAllByListUserName(List<String> usernameList);

}