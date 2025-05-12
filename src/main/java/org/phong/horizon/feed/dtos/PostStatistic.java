package org.phong.horizon.feed.dtos;

public record PostStatistic(
        Long totalLikes,
        Long totalComments,
        Long totalBookmarks,
        Long totalViews,
        Boolean isLiked,
        Boolean isBookmarked
) {}