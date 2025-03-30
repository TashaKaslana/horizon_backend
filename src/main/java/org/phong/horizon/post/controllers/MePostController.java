package org.phong.horizon.post.controllers;

import org.phong.horizon.post.dtos.CreatePostRequest;
import org.phong.horizon.post.dtos.PostRespond;
import org.phong.horizon.post.dtos.UpdatePostRequest;
import org.phong.horizon.post.services.PostService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/me/posts")
public class MePostController {
    private final PostService postService;

    public MePostController(PostService postService) {
        this.postService = postService;
    }

    @PostMapping
    public ResponseEntity<UUID> createPost(@RequestBody CreatePostRequest request) {
        UUID postId = postService.createPost(request);
        return ResponseEntity.ok(postId);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updatePost(@PathVariable UUID id, @RequestBody UpdatePostRequest request) {
        postService.updatePost(id, request);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable UUID id) {
        postService.deleteMePost(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<PostRespond>> getMyPosts() {
        return ResponseEntity.ok(postService.getMeAllPost());
    }
}

