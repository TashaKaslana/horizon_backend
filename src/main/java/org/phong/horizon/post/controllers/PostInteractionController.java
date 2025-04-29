package org.phong.horizon.post.controllers;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.phong.horizon.core.enums.SystemCategory;
import org.phong.horizon.historyactivity.annotations.LogActivity;
import org.phong.horizon.core.enums.InteractionType;
import org.phong.horizon.historyactivity.enums.ActivityTypeCode;
import org.phong.horizon.post.dtos.CreatePostInteraction;
import org.phong.horizon.post.dtos.PostInteractionResponse;
import org.phong.horizon.post.services.PostInteractionService;
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
@RequestMapping("/api/posts/{postId}/interactions")
@AllArgsConstructor
public class PostInteractionController {
    private final PostInteractionService postInteractionService;

    @PostMapping
    @LogActivity(
            activityCode = ActivityTypeCode.POST_LIKE,
            description = "Create a new post interaction",
            targetType = SystemCategory.POST,
            targetIdExpression = "#postId"
    )
    public ResponseEntity<RestApiResponse<Void>> createInteraction(@PathVariable UUID postId,
                                                  @Valid @RequestBody CreatePostInteraction postInteraction) {
        postInteractionService.createInteraction(postId, postInteraction);
        return RestApiResponse.noContent();
    }

    @DeleteMapping("/{interactionType}")
    @LogActivity(
            activityCode = ActivityTypeCode.POST_UNLIKE,
            description = "Delete a post interaction",
            targetType = SystemCategory.POST,
            targetIdExpression = "#postId"
    )
    public ResponseEntity<RestApiResponse<Void>> deleteInteraction(@PathVariable UUID postId,
                                                  @PathVariable String interactionType) {
        postInteractionService.removeInteraction(postId, InteractionType.fromString(interactionType));
        return RestApiResponse.noContent();
    }

    @GetMapping("/{interactionType}/me-is-interacted")
    public ResponseEntity<RestApiResponse<Boolean>> hasMeInteracted(@PathVariable UUID postId, @PathVariable String interactionType) {
        boolean hasInteracted = postInteractionService.hasUserInteracted(postId, InteractionType.fromString(interactionType));
        return RestApiResponse.success(hasInteracted);
    }

    @GetMapping()
    public ResponseEntity<RestApiResponse<List<PostInteractionResponse>>> getInteractionsByPostId(@PathVariable UUID postId) {
        List<PostInteractionResponse> interactions = postInteractionService.getInteractionsByPostId(postId);
        return RestApiResponse.success(interactions);
    }
}
