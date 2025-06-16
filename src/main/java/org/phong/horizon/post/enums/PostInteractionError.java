package org.phong.horizon.post.enums;

import lombok.Getter;

@Getter
public enum PostInteractionError {
    POST_INTERACTION_EXISTS("post_interaction.error.exists"),
    POST_INTERACTION_NOT_FOUND("post_interaction.error.not_found"),
    POST_INTERACTION_INVALID("post_interaction.error.invalid"),
    POST_INTERACTION_NOT_AUTHORIZED("post_interaction.error.not_authorized"),
    POST_INTERACTION_TOO_LONG("post_interaction.error.too_long"),
    POST_INTERACTION_ALREADY_DELETED("post_interaction.error.already_deleted"),
    POST_INTERACTION_UNAUTHORIZED_ACCESS("post_interaction.error.unauthorized_access");

    private final String messageKey;

    PostInteractionError(String messageKey) {
        this.messageKey = messageKey;
    }

    public String getMessage(Object... args) {
        return org.phong.horizon.core.config.LocalizationProvider.getMessage(this.messageKey, args);
    }
}
