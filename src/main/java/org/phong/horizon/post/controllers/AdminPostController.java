package org.phong.horizon.post.controllers;

import org.phong.horizon.core.enums.SystemCategory;
import org.phong.horizon.historyactivity.annotations.LogActivity;
import org.phong.horizon.historyactivity.enums.ActivityTypeCode;
import org.phong.horizon.post.dtos.PostResponse;
import org.phong.horizon.post.services.PostService;
import org.phong.horizon.core.responses.RestApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/posts")
public class AdminPostController {

    private final PostService postService;

    public AdminPostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping
    public ResponseEntity<RestApiResponse<List<PostResponse>>> getAllPostsForAdmin() {
        return RestApiResponse.success(postService.getAllPostsForAdmin());
    }

    @DeleteMapping("/{postId}")
    @LogActivity(
            activityCode = ActivityTypeCode.POST_DELETE,
            description = "Admin delete a post",
            targetType = SystemCategory.POST,
            targetIdExpression = "#postId"
    )
    public ResponseEntity<RestApiResponse<Void>> deletePost(@PathVariable UUID postId) {
        postService.deletePost(postId);
        return RestApiResponse.noContent();
    }

    @DeleteMapping("/user/{userId}")
    @LogActivity(
            activityCode = ActivityTypeCode.POST_DELETE,
            description = "Admin delete all posts to user",
            targetType = SystemCategory.USER,
            targetIdExpression = "#userId"
    )
    public ResponseEntity<RestApiResponse<Void>> deleteAllPostsByUser(@PathVariable UUID userId) {
        postService.deleteAllPostsByUser(userId);
        return RestApiResponse.noContent();
    }
}
