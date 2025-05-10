package org.phong.horizon.follow.infrastructure.persistence.repositories;

import org.phong.horizon.follow.infrastructure.persistence.entities.Follow;
import org.phong.horizon.follow.infrastructure.persistence.entities.FollowId;
import org.phong.horizon.follow.infrastructure.persistence.projections.FollowOverviewProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface FollowRepository extends JpaRepository<Follow, FollowId> {
  @Query("SELECT EXISTS (SELECT 1 FROM Follow f WHERE f.follower.id =: followerId AND f.following.id = :followingId) " +
          "AND EXISTS (SELECT 1 FROM Follow f WHERE f.follower.id =: followingId AND f.following.id = :followerId)")
  boolean checkIfFriend(UUID followerId, UUID followingId);

  Page<Follow> findAllByFollowing_Id(UUID followingId,
                                     Pageable pageable);

  Page<Follow> findAllByFollower_Id(UUID userId,
                                    Pageable pageable);

  void deleteAllByFollower_Id(UUID followerId);

  void deleteAllByFollowing_Id(UUID userId);

  @Query(value = """
    SELECT
        CASE\s
            WHEN EXISTS (
                SELECT 1 FROM follows f1\s
                WHERE f1.follower_id = :meId AND f1.following_id = :userId
            ) THEN true ELSE false END AS isMeFollowing,
        (SELECT COUNT(*) FROM follows f2 WHERE f2.following_id = :userId) AS totalFollowers,
        (SELECT COUNT(*) FROM follows f3 WHERE f3.follower_id = :userId) AS totalFollowing
       FROM (SELECT 1) AS dummy
   \s""", nativeQuery = true)
  FollowOverviewProjection getFollowOverviewProjectionByUserId(
          @Param("userId") UUID userId,
          @Param("meId") UUID meId
  );
}