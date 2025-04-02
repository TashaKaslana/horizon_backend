package org.phong.horizon.comment.enums;

import lombok.Getter;

@Getter
public enum CommentInteractionError {
    COMMENT_INTERACTION_EXISTS("Comment interaction already exists"),
    COMMENT_INTERACTION_NOT_FOUND("Comment interaction not found"),
    COMMENT_INTERACTION_INVALID("Invalid comment interaction"),
    COMMENT_INTERACTION_NOT_AUTHORIZED("User is not authorized to perform this action"),
    COMMENT_INTERACTION_TOO_LONG("Comment interaction exceeds the maximum allowed length"),
    COMMENT_INTERACTION_ALREADY_DELETED("This comment interaction has already been deleted"),
    COMMENT_INTERACTION_UNAUTHORIZED_ACCESS("Access to the comment interaction is denied");

    private final String message;

    CommentInteractionError(String message) {
        this.message = message;
    }
}
