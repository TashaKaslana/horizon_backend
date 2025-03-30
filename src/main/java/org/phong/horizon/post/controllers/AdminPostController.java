package org.phong.horizon.post.controllers;

import org.phong.horizon.post.dtos.PostRespond;
import org.phong.horizon.post.dtos.DeletePostOfUserRequest;
import org.phong.horizon.post.services.PostService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public ResponseEntity<List<PostRespond>> getAllPosts() {
        return ResponseEntity.ok(postService.getAll());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable UUID id) {
        postService.deletePost(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping()
    public ResponseEntity<Void> deleteAllPosts(DeletePostOfUserRequest request) {
        postService.deleteAll(request.userId());

        return ResponseEntity.noContent().build();
    }
}

