package org.phong.horizon.post.infrastructure.persistence.repositories;

import org.phong.horizon.post.infrastructure.persistence.entities.PostView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface ViewRepository extends JpaRepository<PostView, UUID> {
  boolean existsByPostIdAndUserIdAndViewedAtAfter(UUID postId, UUID userId, LocalDateTime time);
  boolean existsByPostIdAndIpAddressAndViewedAtAfter(UUID postId, String ipAddress, LocalDateTime time);

  long countByPostId(UUID postId);

  @Query("SELECT pv.postId, COUNT(pv) FROM PostView pv WHERE pv.postId IN :postIds GROUP BY pv.postId")
  List<Object[]> countViewsByPostIds(@Param("postIds") List<UUID> postIds);

  long countAllByUserIdIs(UUID userId);

    long countAllByPostId(UUID postId);
}