package org.phong.horizon.post.controllers;

import jakarta.validation.Valid;
import org.phong.horizon.core.enums.SystemCategory;
import org.phong.horizon.historyactivity.annotations.LogActivity;
import org.phong.horizon.historyactivity.enums.ActivityTypeCode;
import org.phong.horizon.post.dtos.CreatePostRequest;
import org.phong.horizon.post.dtos.PostCreatedDto;
import org.phong.horizon.post.dtos.PostRespond;
import org.phong.horizon.post.dtos.UpdatePostRequest;
import org.phong.horizon.post.services.PostService;
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
    public ResponseEntity<PostRespond> getPostById(@PathVariable UUID postId) {
        return ResponseEntity.ok(postService.getPostById(postId));
    }

    @GetMapping("/me")
    public ResponseEntity<List<PostRespond>> getMeAllPosts() {
        return ResponseEntity.ok(postService.getMeAllPosts());
    }

    @GetMapping("/public")
    public ResponseEntity<List<PostRespond>> getAllPublicPosts() {
        return ResponseEntity.ok(postService.getAllPublicPosts());
    }

    @GetMapping("/user/{userId}/public")
    public ResponseEntity<List<PostRespond>> getAllPublicPostsByUserId(@PathVariable UUID userId) {
        return ResponseEntity.ok(postService.getAllPublicPostsByUserId(userId));
    }


    @PostMapping
    @LogActivity(
            activityCode = ActivityTypeCode.POST_CREATE,
            description = "Create a new post",
            targetType = SystemCategory.POST,
            targetIdExpression = "#result.body.id"
    )
    public ResponseEntity<PostCreatedDto> createPost(@Valid @RequestBody CreatePostRequest request) {
        return ResponseEntity.ok(postService.createPost(request));
    }

    @PutMapping("/{postId}")
    public ResponseEntity<Void> updatePost(@PathVariable UUID postId,
                                           @Valid @RequestBody UpdatePostRequest request) {
        postService.updatePost(postId, request);
        return ResponseEntity.noContent().build();
    }


    @DeleteMapping("/{postId}")
    @LogActivity(
            activityCode = ActivityTypeCode.POST_DELETE,
            description = "Post entity deleted",
            targetType = SystemCategory.POST,
            targetIdExpression = "#postId"
    )
    public ResponseEntity<Void> deletePost(@PathVariable UUID postId) {
        postService.deletePost(postId);
        return ResponseEntity.noContent().build();
    }
}
