package org.phong.horizon.follow.infrastructure.persistence.repositories;

import org.phong.horizon.follow.infrastructure.persistence.entities.Follow;
import org.phong.horizon.follow.infrastructure.persistence.entities.FollowId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

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
}