package org.phong.horizon.feed.controllers;

import lombok.AllArgsConstructor;
import org.phong.horizon.feed.dtos.FeedPage;
import org.phong.horizon.feed.services.FeedService;
import org.phong.horizon.core.responses.RestApiResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/feeds")
@AllArgsConstructor
public class FeedController {
    private final FeedService feedService;

    @GetMapping()
    public ResponseEntity<RestApiResponse<List<FeedPage>>> getFeed(Pageable pageable,
                                                                   @RequestParam(required = false) UUID excludePostId,
                                                                   @RequestParam(required = false) String categoryName) {
        return RestApiResponse.success(feedService.getFeedForMe(pageable, excludePostId, categoryName));
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<RestApiResponse<List<FeedPage>>> getFeedByUserId(Pageable pageable,
                                                                   @PathVariable UUID userId,
                                                                   @RequestParam(required = false) UUID excludePostId) {
        return RestApiResponse.success(feedService.getFeedByUserId(pageable, userId, excludePostId));
    }

    @GetMapping("/posts/{postId}")
    public ResponseEntity<RestApiResponse<FeedPage>> getFeed(@PathVariable UUID postId) {
        return RestApiResponse.success(feedService.getFeedForMe(postId));
    }
}

