package org.phong.horizon.comment.enums;

import lombok.Getter;
import org.phong.horizon.core.services.LocalizationProvider;

@Getter
public enum CommentErrorEnums {
    COMMENT_NOT_FOUND("comment.error.not_found"),
    USER_NOT_AUTHORIZED("comment.error.user_not_authorized"),
    COMMENT_TOO_LONG("comment.error.comment_too_long"),
    INVALID_COMMENT_FORMAT("comment.error.invalid_format"),
    COMMENT_ALREADY_DELETED("comment.error.already_deleted"),
    UNAUTHORIZED_ACCESS("comment.error.unauthorized_access"),
    MISSING_COMMENT_CONTENT("comment.error.missing_content"),
    INVALID_USER("comment.error.invalid_user"),
    COMMENT_ALREADY_PINNED("comment.error.already_pinned"),
    COMMENT_ALREADY_UNPINNED("comment.error.already_unpinned"),
    COMMENT_PINNED("comment.success.pinned"),
    COMMENT_UNPINNED("comment.success.unpinned"),
    COMMENT_DELETED("comment.success.deleted"),
    COMMENT_REPORTED("comment.success.reported");
    private final String messageKey;

    CommentErrorEnums(String messageKey) {
        this.messageKey = messageKey;
    }

    public String getMessage(Object... args) {
        return LocalizationProvider.getMessage(this.messageKey, args);
    }
}

