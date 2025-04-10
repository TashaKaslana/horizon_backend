package org.phong.horizon.follow.controllers;

import lombok.RequiredArgsConstructor;
import org.phong.horizon.core.enums.SystemCategory;
import org.phong.horizon.follow.dtos.FollowOneSideRespond;
import org.phong.horizon.follow.services.FollowService;
import org.phong.horizon.historyactivity.annotations.LogActivity;
import org.phong.horizon.historyactivity.enums.ActivityTypeCode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/follows")
@RequiredArgsConstructor
public class FollowController {
    private final FollowService followService;

    @GetMapping("/me/followers")
    public ResponseEntity<Page<FollowOneSideRespond>> getMyFollowers(Pageable pageable) {
        return ResponseEntity.ok(followService.getMyFollowers(pageable));
    }

    @GetMapping("/me/following")
    public ResponseEntity<Page<FollowOneSideRespond>> getMyFollowing(Pageable pageable) {
        return ResponseEntity.ok(followService.getMyFollowing(pageable));
    }

    @GetMapping("/{userId}/followers")
    public ResponseEntity<Page<FollowOneSideRespond>> getFollowersByUserId(@PathVariable UUID userId, Pageable pageable) {
        return ResponseEntity.ok(followService.getFollowersByUserId(userId, pageable));
    }

    @GetMapping("/{userId}/following")
    public ResponseEntity<Page<FollowOneSideRespond>> getFollowingByUserId(@PathVariable UUID userId, Pageable pageable) {
        return ResponseEntity.ok(followService.getFollowingByUserId(userId, pageable));
    }

    @PostMapping("/{followingId}")
    @LogActivity(
            activityCode = ActivityTypeCode.USER_FOLLOW,
            description = "Follow user",
            targetType = SystemCategory.USER,
            targetIdExpression = "#followingId"
    )
    public ResponseEntity<Void> follow(@PathVariable UUID followingId) {
        followService.follow(followingId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{followingId}")
    @LogActivity(
            activityCode = ActivityTypeCode.USER_UNFOLLOW,
            description = "Unfollow user",
            targetType = SystemCategory.USER,
            targetIdExpression = "#followingId"
    )
    public ResponseEntity<Void> unfollow(@PathVariable UUID followingId) {
        followService.unfollow(followingId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/friend-check/{followerId}/{followingId}")
    public ResponseEntity<Boolean> isFriend(
            @PathVariable("followerId") UUID followerId,
            @PathVariable("followingId") UUID followingId
    ) {
        return ResponseEntity.ok(followService.isFriend(followerId, followingId));
    }
}
