package org.phong.horizon.post.enums;

import lombok.Getter;

@Getter
public enum PostInteractionError {
    POST_INTERACTION_EXISTS("Post interaction already exists"),
    POST_INTERACTION_NOT_FOUND("Post interaction not found"),
    POST_INTERACTION_INVALID("Invalid post interaction"),
    POST_INTERACTION_NOT_AUTHORIZED("User is not authorized to perform this action"),
    POST_INTERACTION_TOO_LONG("Post interaction exceeds the maximum allowed length"),
    POST_INTERACTION_ALREADY_DELETED("This post interaction has already been deleted"),
    POST_INTERACTION_UNAUTHORIZED_ACCESS("Access to the post interaction is denied");

    private final String message;

    PostInteractionError(String message) {
        this.message = message;
    }
}
