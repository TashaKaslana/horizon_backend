package org.phong.horizon.comment.controllers;

import org.phong.horizon.comment.dtos.CommentCreatedDto;
import org.phong.horizon.comment.dtos.CommentRespond;
import org.phong.horizon.comment.dtos.CreateCommentDto;
import org.phong.horizon.comment.dtos.UpdateCommentContentDto;
import org.phong.horizon.comment.services.CommentService;
import org.phong.horizon.core.enums.SystemCategory;
import org.phong.horizon.historyactivity.annotations.LogActivity;
import org.phong.horizon.historyactivity.enums.ActivityTypeCode;
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
            targetIdExpression = "#result.body.id"
    )
    public ResponseEntity<CommentCreatedDto> createComment(@RequestBody CreateCommentDto createCommentDto) {
        return ResponseEntity.ok(commentService.createComment(createCommentDto));
    }

    @GetMapping("/{commentId}")
    public ResponseEntity<CommentRespond> getCommentById(@PathVariable UUID commentId) {
        return ResponseEntity.ok(commentService.getCommentById(commentId));
    }

    @GetMapping("/post/{postId}")
    public ResponseEntity<List<CommentRespond>> getAllCommentsByPostId(@PathVariable UUID postId) {
        return ResponseEntity.ok(commentService.getAllCommentsByPostId(postId));
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<Void> updateComment(@PathVariable UUID commentId, 
                                              @RequestBody UpdateCommentContentDto updateCommentContentDto) {
        commentService.updateCommentContent(commentId, updateCommentContentDto);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{commentId}")
    @LogActivity(
            activityCode = ActivityTypeCode.COMMENT_DELETE,
            description = "Delete a comment",
            targetType = SystemCategory.COMMENT,
            targetIdExpression = "#commentId"
    )
    public ResponseEntity<Void> deleteComment(@PathVariable UUID commentId) {
        commentService.deleteCommentById(commentId);
        return ResponseEntity.noContent().build();
    }
}