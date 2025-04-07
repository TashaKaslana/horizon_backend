package org.phong.horizon.post.controllers;

import org.phong.horizon.historyactivity.annotations.LogActivity;
import org.phong.horizon.post.dtos.PostRespond;
import org.phong.horizon.post.services.PostService;
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
    public ResponseEntity<List<PostRespond>> getAllPostsForAdmin() {
        return ResponseEntity.ok(postService.getAllPostsForAdmin());
    }

    @DeleteMapping("/{postId}")
    @LogActivity(
            activityCode = "post_delete",
            description = "Admin delete a post",
            targetType = "POST",
            targetIdExpression = "#postId"
    )
    public ResponseEntity<Void> deletePost(@PathVariable UUID postId) {
        postService.deletePost(postId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/user/{userId}")
    @LogActivity(
            activityCode = "post_delete_all_by_user",
            description = "Admin delete all posts to user",
            targetType = "USER",
            targetIdExpression = "#userId"
    )
    public ResponseEntity<Void> deleteAllPostsByUser(@PathVariable UUID userId) {
        postService.deleteAllPostsByUser(userId);
        return ResponseEntity.noContent().build();
    }
}
