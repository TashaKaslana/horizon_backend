package org.phong.horizon.feed.dtos;

import org.phong.horizon.post.dtos.PostRespond;

public record FeedPage(PostRespond post, PostStatistic statistic) {
}
