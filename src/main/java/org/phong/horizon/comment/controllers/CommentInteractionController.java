package org.phong.horizon.comment.controllers;

import jakarta.validation.Valid;
import org.phong.horizon.comment.dtos.CommentInteractionRespond;
import org.phong.horizon.comment.dtos.CreateCommentInteraction;
import org.phong.horizon.core.enums.SystemCategory;
import org.phong.horizon.historyactivity.annotations.LogActivity;
import org.phong.horizon.core.enums.InteractionType;
import org.phong.horizon.comment.services.CommentInteractionService;
import org.phong.horizon.historyactivity.enums.ActivityTypeCode;
import org.phong.horizon.core.responses.RestApiResponse;
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
    public ResponseEntity<RestApiResponse<List<CommentInteractionRespond>>> getInteractions(@PathVariable UUID commentId) {
        List<CommentInteractionRespond> interactions = interactionService.getInteractionsByCommentId(commentId);
        return RestApiResponse.success(interactions);
    }

    @PostMapping
    @LogActivity(
            activityCode = ActivityTypeCode.COMMENT_INTERACTION_CREATE,
            description = "Create a new comment interaction",
            targetType = SystemCategory.COMMENT,
            targetIdExpression = "#commentId"
    )
    public ResponseEntity<RestApiResponse<CommentInteractionRespond>> createInteraction(@PathVariable UUID commentId,
                                                                                        @Valid @RequestBody CreateCommentInteraction request) {
        return RestApiResponse.created(interactionService.createInteraction(commentId, request));
    }

    @DeleteMapping("/{interactionType}")
    @LogActivity(
            activityCode = ActivityTypeCode.COMMENT_INTERACTION_DELETE,
            description = "Delete a comment interaction",
            targetType = SystemCategory.COMMENT,
            targetIdExpression = "#commentId"
    )
    public ResponseEntity<RestApiResponse<Void>> removeInteraction(@PathVariable UUID commentId,
                                                  @PathVariable String interactionType) {
        interactionService.deleteInteraction(commentId, InteractionType.fromString(interactionType));
        return RestApiResponse.noContent();
    }
}
