package org.phong.horizon.feed.dtos;

public record PostStatistic(
        Long totalLikes,
        Long totalComments,
        Long totalBookmarks,
        Boolean isLiked,
        Boolean isBookmarked
) {}