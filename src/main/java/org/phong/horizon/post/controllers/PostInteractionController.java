package org.phong.horizon.post.controllers;

import lombok.AllArgsConstructor;
import org.phong.horizon.infrastructure.enums.InteractionType;
import org.phong.horizon.post.dtos.CreatePostInteraction;
import org.phong.horizon.post.dtos.PostInteractionRespond;
import org.phong.horizon.post.services.PostInteractionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/posts/{postId}/interactions")
@AllArgsConstructor
public class PostInteractionController {
    private final PostInteractionService postInteractionService;

    @PostMapping
    public ResponseEntity<Void> createInteraction(@PathVariable UUID postId,
                                                  @RequestBody CreatePostInteraction postInteraction) {
        postInteractionService.createInteraction(postId, postInteraction);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{interactionType}")
    public ResponseEntity<Void> deleteInteraction(@PathVariable UUID postId, @PathVariable String interactionType) {
        postInteractionService.deleteInteraction(postId, InteractionType.fromString(interactionType));
        return ResponseEntity.noContent().build();
    }

    @GetMapping()
    public ResponseEntity<List<PostInteractionRespond>> getInteractionsByPostId(@PathVariable UUID postId) {
        List<PostInteractionRespond> interactions = postInteractionService.getInteractionsByPostId(postId);
        return ResponseEntity.ok(interactions);
    }
}