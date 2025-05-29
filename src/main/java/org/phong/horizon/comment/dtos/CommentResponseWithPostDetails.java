package org.phong.horizon.comment.dtos;

import org.phong.horizon.comment.enums.CommentStatus;
import org.phong.horizon.post.dtos.PostSummaryResponse;
import org.phong.horizon.user.dtos.UserSummaryRespond;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

/**
 * DTO for {@link org.phong.horizon.comment.infrastructure.persistence.entities.Comment}
 */
public record CommentResponseWithPostDetails(Instant createdAt, Instant updatedAt, UUID createdBy, UUID updatedBy,
                                             UUID id, PostSummaryResponse post, UserSummaryRespond user, String content,
                                             UUID parentCommentId, UserSummaryRespond parentCommentUser,
                                             String parentCommentContent, Boolean isPinned, Boolean isAuthorDeleted,
                                             CommentStatus status) implements Serializable {
}