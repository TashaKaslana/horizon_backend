package org.phong.horizon.post.controllers;

import org.phong.horizon.historyactivity.annotations.LogActivity;
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

    @LogActivity(
            activityCode = "post_create",
            description = "Create a new post",
            targetType = "POST",
            targetIdExpression = "#result.body.id"
    )
    @PostMapping
    public ResponseEntity<PostCreatedDto> createPost(@RequestBody CreatePostRequest request) {
        return ResponseEntity.ok(postService.createPost(request));
    }

    @PutMapping("/{postId}")
    public ResponseEntity<Void> updatePost(@PathVariable UUID postId, @RequestBody UpdatePostRequest request) {
        postService.updatePost(postId, request);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(@PathVariable UUID postId) {
        postService.deletePost(postId);
        return ResponseEntity.noContent().build();
    }
}
