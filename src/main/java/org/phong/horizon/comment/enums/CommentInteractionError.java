package org.phong.horizon.comment.enums;

import lombok.Getter;

@Getter
public enum CommentInteractionError {
    COMMENT_INTERACTION_EXISTS("comment_interaction.error.exists"),
    COMMENT_INTERACTION_NOT_FOUND("comment_interaction.error.not_found"),
    COMMENT_INTERACTION_INVALID("comment_interaction.error.invalid"),
    COMMENT_INTERACTION_NOT_AUTHORIZED("comment_interaction.error.not_authorized"),
    COMMENT_INTERACTION_TOO_LONG("comment_interaction.error.too_long"),
    COMMENT_INTERACTION_ALREADY_DELETED("comment_interaction.error.already_deleted"),
    COMMENT_INTERACTION_UNAUTHORIZED_ACCESS("comment_interaction.error.unauthorized_access");

    private final String messageKey;

    CommentInteractionError(String messageKey) {
        this.messageKey = messageKey;
    }

    public String getMessage(Object... args) {
        return org.phong.horizon.core.config.LocalizationProvider.getMessage(this.messageKey, args);
    }
}
