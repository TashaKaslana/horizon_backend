package org.phong.horizon.post.controllers;

import org.phong.horizon.post.dtos.PostRespond;
import org.phong.horizon.post.services.PostService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/posts")
public class PostController {
    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostRespond> getPostById(@PathVariable UUID id) {
        return ResponseEntity.ok(postService.getPostById(id));
    }

    @GetMapping()
    public ResponseEntity<List<PostRespond>> getAllPublicPosts() {
        return ResponseEntity.ok(postService.getAllPublicPosts());
    }

    @GetMapping("/by-user")
    public ResponseEntity<List<PostRespond>> getAllPublicPostsByUserId(@RequestParam UUID userId) {
        return ResponseEntity.ok(postService.getAllPublicPostsByUserId(userId));
    }
}

