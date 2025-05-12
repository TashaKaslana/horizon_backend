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
import org.phong.horizon.post.services.PostViewService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FeedService {
    PostService postService;
    CommentService commentService;
    PostInteractionService postInteractionService;
    PostBookmarkService postBookmarkService;
    PostViewService postViewService;

    public Page<FeedPage> getFeedForMe(Pageable pageable, UUID excludePostId, String categoryName) {
        Page<PostResponse> feedPage = postService.getAllPublicPosts(pageable, excludePostId, categoryName);
        return getFeedPages(feedPage);
    }

    public FeedPage getFeedForMe(UUID postId) {
        PostResponse post = postService.getPostById(postId);
        List<UUID> idList = List.of(postId);

        List<UUID> postInteractionList = postInteractionService.getPostIdsMeInteractedByPostIds(idList);
        List<UUID> postBookmarkList = postBookmarkService.getMeBookmarkedIdsByPostId(idList);
        Map<UUID, Long> totalListView = postViewService.getTotalListView(idList);

        PostStatistic statistic = new PostStatistic(
                postInteractionService.getCountInteractionByPostId(post.id()),
                commentService.getCountCommentsByPostId(post.id()),
                postBookmarkService.getCountBookmarksByPostId(post.id()),
                totalListView.getOrDefault(post.id(),  0L),
                postInteractionList.contains(post.id()),
                postBookmarkList.contains(post.id())
        );
        return new FeedPage(post, statistic);
    }

    public Page<FeedPage> getFeedByUserId(Pageable pageable, UUID userId, UUID excludePostId) {
        Page<PostResponse> posts = postService.getAllPublicPostsByUserId(pageable, userId, excludePostId);
        return getFeedPages(posts);
    }

    private Page<FeedPage> getFeedPages(Page<PostResponse> posts) {
        Result result = getStatisticResult(posts);

        return posts.map(post -> {
            UUID postId = post.id();

            PostStatistic statistic = new PostStatistic(
                    result.interactionCountMap().getOrDefault(postId, 0L),
                    result.commentCountMap().getOrDefault(postId, 0L),
                    result.bookmarkCountMap().getOrDefault(postId, 0L),
                    result.totalListView().getOrDefault(postId, 0L),
                    result.postInteractionList().contains(postId),
                    result.postBookmarkList().contains(postId)
            );

            return new FeedPage(post, statistic);
        });

    }

    private Result getStatisticResult(Page<PostResponse> posts) {
        List<UUID> idList = posts.toList().stream().map(PostResponse::id).toList();

        List<UUID> postInteractionList = postInteractionService.getPostIdsMeInteractedByPostIds(idList);
        List<UUID> postBookmarkList = postBookmarkService.getMeBookmarkedIdsByPostId(idList);
        Map<UUID, Long> totalListView = postViewService.getTotalListView(idList);
        Map<UUID, Long> interactionCountMap = postInteractionService.getCountInteractionByPostIds(idList);
        Map<UUID, Long> commentCountMap = commentService.getCountCommentsByPostIds(idList);
        Map<UUID, Long> bookmarkCountMap = postBookmarkService.getCountBookmarksByPostIds(idList);

        return new Result(
                postInteractionList,
                postBookmarkList,
                totalListView,
                interactionCountMap,
                commentCountMap,
                bookmarkCountMap
        );
    }

    private record Result(
            List<UUID> postInteractionList,
            List<UUID> postBookmarkList,
            Map<UUID, Long> totalListView,
            Map<UUID, Long> interactionCountMap,
            Map<UUID, Long> commentCountMap,
            Map<UUID, Long> bookmarkCountMap
    ) {}
}
