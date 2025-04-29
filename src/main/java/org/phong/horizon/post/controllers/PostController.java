package org.phong.horizon.post.controllers;

import jakarta.validation.Valid;
import org.phong.horizon.core.enums.SystemCategory;
import org.phong.horizon.historyactivity.annotations.LogActivity;
import org.phong.horizon.historyactivity.enums.ActivityTypeCode;
import org.phong.horizon.post.dtos.CreatePostRequest;
import org.phong.horizon.post.dtos.PostCreatedDto;
import org.phong.horizon.post.dtos.PostResponse;
import org.phong.horizon.post.dtos.UpdatePostRequest;
import org.phong.horizon.post.services.PostService;
import org.phong.horizon.core.responses.RestApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping("/{postId}")
    public ResponseEntity<RestApiResponse<PostResponse>> getPostById(@PathVariable UUID postId) {
        return RestApiResponse.success(postService.getPostById(postId));
    }

    @GetMapping("/me")
    public ResponseEntity<RestApiResponse<List<PostResponse>>> getMeAllPosts() {
        return RestApiResponse.success(postService.getMeAllPosts());
    }

    @GetMapping("/public")
    public ResponseEntity<RestApiResponse<List<PostResponse>>> getAllPublicPosts() {
        return RestApiResponse.success(postService.getAllPublicPosts());
    }

    @GetMapping("/users/{userId}/public")
    public ResponseEntity<RestApiResponse<List<PostResponse>>> getAllPublicPostsByUserId(@PathVariable UUID userId) {
        return RestApiResponse.success(postService.getAllPublicPostsByUserId(userId));
    }

    @PostMapping
    @LogActivity(
            activityCode = ActivityTypeCode.POST_CREATE,
            description = "Create a new post",
            targetType = SystemCategory.POST,
            targetIdExpression = "#result.body.data.id"
    )
    public ResponseEntity<RestApiResponse<PostCreatedDto>> createPost(@Valid @RequestBody CreatePostRequest request) {
        return RestApiResponse.created(postService.createPost(request));
    }

    @PutMapping("/{postId}")
    public ResponseEntity<RestApiResponse<Void>> updatePost(@PathVariable UUID postId,
                                           @Valid @RequestBody UpdatePostRequest request) {
        postService.updatePost(postId, request);
        return RestApiResponse.noContent();
    }

    @DeleteMapping("/{postId}")
    @LogActivity(
            activityCode = ActivityTypeCode.POST_DELETE,
            description = "Post entity deleted",
            targetType = SystemCategory.POST,
            targetIdExpression = "#postId"
    )
    public ResponseEntity<RestApiResponse<Void>> deletePost(@PathVariable UUID postId) {
        postService.deletePost(postId);
        return RestApiResponse.noContent();
    }
}
