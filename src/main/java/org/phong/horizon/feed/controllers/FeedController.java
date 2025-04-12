package org.phong.horizon.feed.controllers;

import lombok.AllArgsConstructor;
import org.phong.horizon.feed.dtos.FeedPage;
import org.phong.horizon.feed.services.FeedService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/feed")
@AllArgsConstructor
public class FeedController {
    private final FeedService feedService;

    @GetMapping()
    public Page<FeedPage> getFeed(Pageable pageable) {
        return feedService.getFeedForMe(pageable);
    }
}
