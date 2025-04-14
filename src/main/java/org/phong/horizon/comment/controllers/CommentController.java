package org.phong.horizon.comment.controllers;

import jakarta.validation.Valid;
import org.phong.horizon.comment.dtos.CommentCreatedDto;
import org.phong.horizon.comment.dtos.CommentRespond;
import org.phong.horizon.comment.dtos.CreateCommentDto;
import org.phong.horizon.comment.dtos.UpdateCommentContentDto;
import org.phong.horizon.comment.services.CommentService;
import org.phong.horizon.core.enums.SystemCategory;
import org.phong.horizon.historyactivity.annotations.LogActivity;
import org.phong.horizon.historyactivity.enums.ActivityTypeCode;
import org.phong.horizon.core.responses.RestApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/comments")
public class CommentController {
    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping
    @LogActivity(
            activityCode = ActivityTypeCode.COMMENT_CREATE,
            description = "Create a new comment",
            targetType = SystemCategory.POST,
            targetIdExpression = "#result.body.data.id"
    )
    public ResponseEntity<RestApiResponse<CommentCreatedDto>> createComment(@Valid @RequestBody CreateCommentDto request) {
        return RestApiResponse.created(commentService.createComment(request));
    }

    @GetMapping("/{commentId}")
    public ResponseEntity<RestApiResponse<CommentRespond>> getCommentById(@PathVariable UUID commentId) {
        return RestApiResponse.success(commentService.getCommentById(commentId));
    }

    @GetMapping("/post/{postId}")
    public ResponseEntity<RestApiResponse<List<CommentRespond>>> getAllCommentsByPostId(@PathVariable UUID postId) {
        return RestApiResponse.success(commentService.getAllCommentsByPostId(postId));
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<RestApiResponse<Void>> updateComment(@PathVariable UUID commentId, 
                                              @Valid @RequestBody UpdateCommentContentDto request) {
        commentService.updateCommentContent(commentId, request);
        return RestApiResponse.noContent();
    }

    @DeleteMapping("/{commentId}")
    @LogActivity(
            activityCode = ActivityTypeCode.COMMENT_DELETE,
            description = "Delete a comment",
            targetType = SystemCategory.COMMENT,
            targetIdExpression = "#commentId"
    )
    public ResponseEntity<RestApiResponse<Void>> deleteComment(@PathVariable UUID commentId) {
        commentService.deleteCommentById(commentId);
        return RestApiResponse.noContent();
    }
}
