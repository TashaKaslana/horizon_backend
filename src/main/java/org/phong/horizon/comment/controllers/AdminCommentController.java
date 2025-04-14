package org.phong.horizon.comment.controllers;

import lombok.AllArgsConstructor;
import org.phong.horizon.comment.services.CommentService;
import org.phong.horizon.core.enums.SystemCategory;
import org.phong.horizon.core.responses.RestApiResponse;
import org.phong.horizon.historyactivity.annotations.LogActivity;
import org.phong.horizon.historyactivity.enums.ActivityTypeCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/admin/comments")
@AllArgsConstructor
public class AdminCommentController {
    private final CommentService commentService;

    @DeleteMapping("/all")
    @LogActivity(
            activityCode = ActivityTypeCode.COMMENT_DELETE,
            description = "Admin delete all comments",
            targetType = SystemCategory.COMMENT
    )
    public ResponseEntity<RestApiResponse<Void>> deleteAllComments() {
        commentService.deleteAllComments();
        return RestApiResponse.noContent();
    }
}

