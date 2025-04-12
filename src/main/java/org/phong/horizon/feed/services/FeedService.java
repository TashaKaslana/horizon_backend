package org.phong.horizon.feed.services;

import lombok.AllArgsConstructor;
import org.phong.horizon.comment.services.CommentService;
import org.phong.horizon.feed.dtos.FeedPage;
import org.phong.horizon.feed.dtos.PostStatistic;
import org.phong.horizon.post.dtos.PostRespond;
import org.phong.horizon.post.services.PostInteractionService;
import org.phong.horizon.post.services.PostService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class FeedService {
    private final PostService postService;
    private final CommentService commentService;
    private final PostInteractionService postInteractionService;

    public Page<FeedPage> getFeedForMe(Pageable pageable) {
        Page<PostRespond> feedPage = postService.getAllPublicPosts(pageable);
        return feedPage.map(post -> {
            PostStatistic statistic = new PostStatistic(
                    postInteractionService.getCountInteractionByPostId(post.id()),
                    commentService.getCountCommentsByPostId(post.id())
            );
            return new FeedPage(post, statistic);
        });
    }
}
