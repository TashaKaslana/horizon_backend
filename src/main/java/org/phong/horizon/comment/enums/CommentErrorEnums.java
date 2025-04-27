package org.phong.horizon.comment.enums;

import lombok.Getter;

@Getter
public enum CommentErrorEnums {
    COMMENT_NOT_FOUND("Comment not found"),
    USER_NOT_AUTHORIZED("User is not authorized to perform this action"),
    COMMENT_TOO_LONG("Comment exceeds the maximum allowed length"),
    INVALID_COMMENT_FORMAT("Comment format is invalid"),
    COMMENT_ALREADY_DELETED("This comment has already been deleted"),
    UNAUTHORIZED_ACCESS("Access to the comment is denied"),
    MISSING_COMMENT_CONTENT("Comment content cannot be empty"),
    INVALID_USER("The user does not exist"),
    COMMENT_ALREADY_PINNED("Comment is already pinned"),
    COMMENT_ALREADY_UNPINNED("Comment is already unpinned"),
    COMMENT_PINNED("Comment pinned successfully"),
    COMMENT_UNPINNED("Comment unpinned successfully"),
    COMMENT_DELETED("Comment deleted successfully"),
    COMMENT_REPORTED("Comment reported successfully");
    private final String message;

    CommentErrorEnums(String message) {
        this.message = message;
    }
}

