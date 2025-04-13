package org.phong.horizon.user.infrastructure.persistence.repositories;

import org.phong.horizon.user.infrastructure.persistence.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByAuth0Id(String auth0Id);

    @Query("SELECT u.id FROM User u WHERE u.auth0Id = :auth0Id")
    UUID getUserIdByAuth0Id(String auth0Id);

    @Query("SELECT u FROM User u WHERE u.username IN :usernameList")
    List<User> findAllByListUserName(List<String> usernameList);

    @Modifying
    @Query("UPDATE User u SET u.deletedAt = CURRENT_TIMESTAMP WHERE u.id = :id")
    void softDeleteById(UUID id);

    @Modifying
    @Query("UPDATE User u SET u.deletedAt = null WHERE u.id = :id")
    void restoreById(@Param("id") UUID id);

    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END " +
            "FROM User u WHERE u.auth0Id = :auth0 OR u.username = :username OR u.email = :email")
    boolean isAlreadyExist(@Param("auth0") String auth0,
                           @Param("username") String username,
                           @Param("email") String email);
}