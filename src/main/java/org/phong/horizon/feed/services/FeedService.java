package org.phong.horizon.feed.services;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.phong.horizon.comment.services.CommentService;
import org.phong.horizon.feed.dtos.FeedPage;
import org.phong.horizon.feed.dtos.PostStatistic;
import org.phong.horizon.post.dtos.PostResponse;
import org.phong.horizon.post.services.PostBookmarkService;
import org.phong.horizon.post.services.PostInteractionService;
import org.phong.horizon.post.services.PostService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FeedService {
    PostService postService;
    CommentService commentService;
    PostInteractionService postInteractionService;
    PostBookmarkService postBookmarkService;

    public Page<FeedPage> getFeedForMe(Pageable pageable, UUID excludePostId) {
        Page<PostResponse> feedPage = postService.getAllPublicPosts(pageable, excludePostId);
        List<UUID> postInteractionList = postInteractionService.getPostIdsInteractedByPostIds(
                feedPage.toList().stream().map(PostResponse::id).toList()
        );
        List<UUID> postBookmarkList = postBookmarkService.getBookmarkedIdsByPostId(
                feedPage.toList().stream().map(PostResponse::id).toList()
        );

        return feedPage.map(post -> {
            PostStatistic statistic = new PostStatistic(
                    postInteractionService.getCountInteractionByPostId(post.id()),
                    commentService.getCountCommentsByPostId(post.id()),
                    postBookmarkService.getCountBookmarksByPostId(post.id()),
                    postInteractionList.contains(post.id()),
                    postBookmarkList.contains(post.id())
            );
            return new FeedPage(post, statistic);
        });
    }
}
