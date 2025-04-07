package org.phong.horizon.follow.services;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.phong.horizon.follow.dtos.FollowOneSideRespond;
import org.phong.horizon.follow.dtos.FollowRespond;
import org.phong.horizon.follow.enums.FollowErrorEnums;
import org.phong.horizon.follow.exceptions.FollowException;
import org.phong.horizon.follow.infrastructure.mapstruct.FollowMapper;
import org.phong.horizon.follow.infrastructure.persistence.entities.Follow;
import org.phong.horizon.follow.infrastructure.persistence.entities.FollowId;
import org.phong.horizon.follow.infrastructure.persistence.repositories.FollowRepository;
import org.phong.horizon.infrastructure.services.AuthService;
import org.phong.horizon.user.services.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@AllArgsConstructor
@Slf4j
public class FollowService {
    private final FollowRepository followRepository;
    private final UserService userService;
    private final AuthService authService;
    private final FollowMapper followMapper;

    public Page<FollowOneSideRespond> getMyFollowers(Pageable pageable) {
        UUID userId = authService.getUserIdFromContext();
        return followRepository.findAllByFollowing_Id(userId, pageable)
                .map(followMapper::mapToFollowerSide);
    }

    public Page<FollowOneSideRespond> getMyFollowing(Pageable pageable) {
        UUID userId = authService.getUserIdFromContext();
        return followRepository.findAllByFollower_Id(userId, pageable)
                .map(followMapper::mapToFollowingSide);
    }

    public Page<FollowOneSideRespond> getFollowersByUserId(UUID userId, Pageable pageable) {
        return followRepository.findAllByFollowing_Id(userId, pageable).map(followMapper::mapToFollowerSide);

    }

    public Page<FollowOneSideRespond> getFollowingByUserId(UUID userId, Pageable pageable) {
        return followRepository.findAllByFollower_Id(userId, pageable)
                .map(followMapper::mapToFollowerSide);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Page<FollowRespond> getAll(Pageable pageable) {
        return followRepository.findAll(pageable).map(followMapper::toDto);
    }

    @Transactional
    public void follow(UUID followingId) {
        UUID followerId = authService.getUserIdFromContext();
        if (followerId.equals(followingId)) {
            throw new FollowException(FollowErrorEnums.UNABLE_TO_FOLLOW_SELF);
        }

        followBetween(followerId, followingId);
    }

    @Transactional
    public void unfollow(UUID followingId) {
        UUID followerId = authService.getUserIdFromContext();
        if (followerId.equals(followingId)) {
            throw new FollowException(FollowErrorEnums.UNABLE_TO_UNFOLLOW_SELF);
        }

        unfollowBetween(followerId, followingId);
    }

    @Transactional
    public void followBetween(UUID followerId, UUID followingId) {
        checkUsersExist(followerId, followingId);
        if (followRepository.existsById(new FollowId(followerId, followingId))) {
            log.warn("Follow relationship already exists: {} -> {}", followerId, followingId);
            return;
        }

        followRepository.save(
                Follow.builder()
                        .id(new FollowId(followerId, followingId))
                        .follower(userService.getRefById(followerId))
                        .following(userService.getRefById(followingId))
                        .build()
        );
    }


    @Transactional
    public void unfollowBetween(UUID followerId, UUID followingId) {
        FollowId followId = new FollowId(followerId, followingId);
        if (!followRepository.existsById(followId)) {
            log.warn("Follow relationship does not exist, cannot unfollow: {} -> {}", followerId, followingId);
            return;
        }

        followRepository.deleteById(
                new FollowId(followerId, followingId)
        );
    }

    @Transactional
    protected void checkUsersExist(UUID followerId, UUID followingId) {
        if (!userService.existsById(followerId)) {
            log.warn("Follower not found: {}", followerId);
            throw new FollowException(FollowErrorEnums.FOLLOWER_NOT_FOUND);
        } else if (!userService.existsById(followingId)) {
            log.warn("Following not found: {}", followingId);
            throw new FollowException(FollowErrorEnums.FOLLOWING_NOT_FOUND);
        }
    }

    public boolean isFriend(UUID followerId, UUID followingId) {
        return followRepository.checkIfFriend(followerId, followingId);
    }
}
