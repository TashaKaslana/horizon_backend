package org.phong.horizon.comment.dtos;

import org.phong.horizon.comment.enums.CommentStatus;

import java.util.List;
import java.util.UUID;

public record BulkCommentUpdateRequest(List<UUID> ids, CommentStatus status) {
}

