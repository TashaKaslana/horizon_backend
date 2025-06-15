package org.phong.horizon.follow.services;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.phong.horizon.core.services.AuthService;
import org.phong.horizon.follow.dtos.FollowOneSideRespond;
import org.phong.horizon.follow.dtos.FollowOverview;
import org.phong.horizon.follow.dtos.FollowRespond;
import org.phong.horizon.follow.enums.FollowErrorEnums;
import org.phong.horizon.follow.events.UserFollowedEvent;
import org.phong.horizon.follow.events.UserUnFollowedEvent;
import org.phong.horizon.follow.exceptions.FollowNotFoundException;
import org.phong.horizon.follow.exceptions.FollowSelfException;
import org.phong.horizon.follow.infrastructure.mapstruct.FollowMapper;
import org.phong.horizon.follow.infrastructure.persistence.entities.Follow;
import org.phong.horizon.follow.infrastructure.persistence.entities.FollowId;
import org.phong.horizon.follow.infrastructure.persistence.projections.FollowOverviewProjection;
import org.phong.horizon.follow.infrastructure.persistence.repositories.FollowRepository;
import org.phong.horizon.user.services.UserService;
import org.springframework.context.ApplicationEventPublisher;
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
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public Page<FollowOneSideRespond> getMyFollowers(Pageable pageable) {
        UUID userId = authService.getUserIdFromContext();
        return followRepository.findAllByFollowing_Id(userId, pageable)
                .map(followMapper::mapToFollowerSide);
    }

    @Transactional
    public Page<FollowOneSideRespond> getMyFollowing(Pageable pageable) {
        UUID userId = authService.getUserIdFromContext();
        return followRepository.findAllByFollower_Id(userId, pageable)
                .map(followMapper::mapToFollowingSide);
    }

    @Transactional
    public Page<FollowOneSideRespond> getFollowersByUserId(UUID userId, Pageable pageable) {
        return followRepository.findAllByFollowing_Id(userId, pageable).map(followMapper::mapToFollowerSide);
    }

    @Transactional
    public Page<FollowOneSideRespond> getFollowingByUserId(UUID userId, Pageable pageable) {
        return followRepository.findAllByFollower_Id(userId, pageable)
                .map(followMapper::mapToFollowerSide);
    }

    public FollowOverview getFollowOverviewById(UUID id) {
        UUID currentUserId = authService.getUserIdFromContext();
        FollowOverviewProjection projection = followRepository.getFollowOverviewProjectionByUserId(id, currentUserId);
        return new FollowOverview(
                projection.getIsMeFollowing(),
                projection.getTotalFollowers(),
                projection.getTotalFollowing()
        );
    }

    @Transactional
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Page<FollowRespond> getAll(Pageable pageable) {
        return followRepository.findAll(pageable).map(followMapper::toDto);
    }

    @Transactional
    public void follow(UUID followingId) {
        UUID followerId = authService.getUserIdFromContext();
        if (followerId.equals(followingId)) {
            throw new FollowSelfException(FollowErrorEnums.UNABLE_TO_FOLLOW_SELF);
        }

        followBetween(followerId, followingId);
    }

    @Transactional
    public void unfollow(UUID followingId) {
        UUID followerId = authService.getUserIdFromContext();
        if (followerId.equals(followingId)) {
            throw new FollowSelfException(FollowErrorEnums.UNABLE_TO_UNFOLLOW_SELF);
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

        eventPublisher.publishEvent(new UserFollowedEvent(
                this,
                userService.findById(followerId).getUsername(),
                userService.findById(followingId).getUsername(),
                followerId,
                followingId
        ));
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

        eventPublisher.publishEvent(new UserUnFollowedEvent(
                this,
                followerId,
                followingId,
                userService.findById(followerId).getUsername(),
                userService.findById(followingId).getUsername()
        ));
    }

    @Transactional
    protected void checkUsersExist(UUID followerId, UUID followingId) {
        if (!userService.existsById(followerId)) {
            log.warn("Follower not found: {}", followerId);
            throw new FollowNotFoundException(FollowErrorEnums.FOLLOWER_NOT_FOUND);
        } else if (!userService.existsById(followingId)) {
            log.warn("Following not found: {}", followingId);
            throw new FollowNotFoundException(FollowErrorEnums.FOLLOWING_NOT_FOUND);
        }
    }

    public boolean isFriend(UUID followerId, UUID followingId) {
        return followRepository.checkIfFriend(followerId, followingId);
    }

    @Transactional
    public void deleteAllByUserID(UUID userId) {
        followRepository.deleteAllByFollower_Id(userId);
        followRepository.deleteAllByFollowing_Id(userId);
    }
}
