package org.phong.horizon.post.infrastructure.persistence.repositories;

import org.phong.horizon.post.infrastructure.persistence.entities.PostView;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.UUID;

public interface ViewRepository extends JpaRepository<PostView, UUID> {
  boolean existsByPostIdAndUserIdAndViewedAtAfter(UUID postId, UUID userId, LocalDateTime time);
  boolean existsByPostIdAndIpAddressAndViewedAtAfter(UUID postId, String ipAddress, LocalDateTime time);
}