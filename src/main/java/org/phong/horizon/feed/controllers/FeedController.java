package org.phong.horizon.feed.controllers;

import lombok.AllArgsConstructor;
import org.phong.horizon.feed.dtos.FeedPage;
import org.phong.horizon.feed.services.FeedService;
import org.phong.horizon.core.responses.RestApiResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/feed")
@AllArgsConstructor
public class FeedController {
    private final FeedService feedService;

    @GetMapping()
    public ResponseEntity<RestApiResponse<List<FeedPage>>> getFeed(Pageable pageable) {
        return RestApiResponse.success(feedService.getFeedForMe(pageable));
    }
}

