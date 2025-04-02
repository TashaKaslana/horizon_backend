package org.phong.horizon.comment.controllers;

import org.phong.horizon.comment.dtos.CommentInteractionRespond;
import org.phong.horizon.comment.dtos.CreateCommentInteraction;
import org.phong.horizon.comment.enums.InteractionType;
import org.phong.horizon.comment.services.CommentInteractionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/comments/{commentId}/interactions")
public class CommentInteractionController {
    private final CommentInteractionService interactionService;

    public CommentInteractionController(CommentInteractionService interactionService) {
        this.interactionService = interactionService;
    }

    @GetMapping
    public ResponseEntity<List<CommentInteractionRespond>> getInteractions(@PathVariable UUID commentId) {
        List<CommentInteractionRespond> interactions = interactionService.getInteractionsByCommentId(commentId);

        return ResponseEntity.ok(interactions);
    }

    @PostMapping
    public ResponseEntity<Void> createInteraction(@PathVariable UUID commentId,
                                                  @RequestBody CreateCommentInteraction createCommentInteraction) {
        interactionService.createInteraction(commentId, createCommentInteraction);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{interactionType}")
    public ResponseEntity<Void> removeInteraction(@PathVariable UUID commentId,
                                                  @PathVariable InteractionType interactionType) {
        interactionService.deleteInteraction(commentId, interactionType);
        return ResponseEntity.noContent().build();
    }
}
