package org.phong.horizon.comment.controllers;

import org.phong.horizon.comment.services.CommentService;
import org.phong.horizon.historyactivity.annotations.LogActivity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/admin/comments")
public class AdminCommentController {
    private final CommentService commentService;

    public AdminCommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @DeleteMapping("/all")
    @LogActivity(
            activityCode = "comment_delete_all",
            description = "Admin delete all comments",
            targetType = "COMMENT"
    )
    public ResponseEntity<Void> deleteAllComments() {
        commentService.deleteAllComments();
        return ResponseEntity.noContent().build();
    }
}
