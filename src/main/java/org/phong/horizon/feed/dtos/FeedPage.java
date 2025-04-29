package org.phong.horizon.feed.dtos;

import org.phong.horizon.post.dtos.PostResponse;

public record FeedPage(PostResponse post, PostStatistic statistic) {
}
