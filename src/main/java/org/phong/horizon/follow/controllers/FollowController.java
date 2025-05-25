package org.phong.horizon.follow.controllers;

import lombok.RequiredArgsConstructor;
import org.phong.horizon.core.enums.SystemCategory;
import org.phong.horizon.core.responses.RestApiResponse;
import org.phong.horizon.follow.dtos.FollowOneSideRespond;
import org.phong.horizon.follow.dtos.FollowOverview;
import org.phong.horizon.follow.services.FollowService;
import org.phong.horizon.historyactivity.annotations.LogActivity;
import org.phong.horizon.historyactivity.enums.ActivityTypeCode;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/follows")
@RequiredArgsConstructor
public class FollowController {
    private final FollowService followService;

    @GetMapping("/me/followers")
    public ResponseEntity<RestApiResponse<List<FollowOneSideRespond>>> getMyFollowers(@ParameterObject Pageable pageable) {
        return RestApiResponse.success(followService.getMyFollowers(pageable));
    }

    @GetMapping("/me/following")
    public ResponseEntity<RestApiResponse<List<FollowOneSideRespond>>> getMyFollowing(@ParameterObject Pageable pageable) {
        return RestApiResponse.success(followService.getMyFollowing(pageable));
    }

    @GetMapping("/{userId}/followers")
    public ResponseEntity<RestApiResponse<List<FollowOneSideRespond>>> getFollowersByUserId(@PathVariable UUID userId, @ParameterObject Pageable pageable) {
        return RestApiResponse.success(followService.getFollowersByUserId(userId, pageable));
    }

    @GetMapping("/{userId}/following")
    public ResponseEntity<RestApiResponse<List<FollowOneSideRespond>>> getFollowingByUserId(@PathVariable UUID userId, @ParameterObject Pageable pageable) {
        return RestApiResponse.success(followService.getFollowingByUserId(userId, pageable));
    }

    @GetMapping("{userId}/overview")
    public ResponseEntity<RestApiResponse<FollowOverview>> getOverview(@PathVariable UUID userId) {
        return RestApiResponse.success(followService.getFollowOverviewById(userId));
    }

    @PostMapping("/{followingId}")
    @LogActivity(
            activityCode = ActivityTypeCode.USER_FOLLOW,
            description = "Follow user",
            targetType = SystemCategory.USER,
            targetIdExpression = "#followingId"
    )
    public ResponseEntity<RestApiResponse<Void>> follow(@PathVariable UUID followingId) {
        followService.follow(followingId);
        return RestApiResponse.created();
    }

    @DeleteMapping("/{followingId}")
    @LogActivity(
            activityCode = ActivityTypeCode.USER_UNFOLLOW,
            description = "Unfollow user",
            targetType = SystemCategory.USER,
            targetIdExpression = "#followingId"
    )
    public ResponseEntity<RestApiResponse<Void>> unfollow(@PathVariable UUID followingId) {
        followService.unfollow(followingId);
        return RestApiResponse.noContent();
    }

    @GetMapping("/friend-check/{followerId}/{followingId}")
    public ResponseEntity<RestApiResponse<Boolean>> isFriend(
            @PathVariable("followerId") UUID followerId,
            @PathVariable("followingId") UUID followingId
    ) {
        return RestApiResponse.success(followService.isFriend(followerId, followingId));
    }
}
