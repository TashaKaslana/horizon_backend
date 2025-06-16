package org.phong.horizon.follow.enums;

import lombok.Getter;

@Getter
public enum FollowErrorEnums {
    FOLLOW_NOT_FOUND("follow.error.not_found"),
    FOLLOW_ALREADY_EXISTS("follow.error.already_exists"),
    FOLLOWER_NOT_FOUND("follow.error.follower_not_found"),
    FOLLOWING_NOT_FOUND("follow.error.following_not_found"),
    INVALID_FOLLOW_REQUEST("follow.error.invalid_request"),
    UNABLE_TO_FOLLOW_SELF("follow.error.unable_follow_self"),
    UNABLE_TO_UNFOLLOW_SELF("follow.error.unable_unfollow_self"),
    NOT_FOLLOWING("follow.error.not_following"),;

    private final String messageKey;

    FollowErrorEnums(String messageKey) {
        this.messageKey = messageKey;
    }

    public String getMessage(Object... args) {
        return org.phong.horizon.core.config.LocalizationProvider.getMessage(this.messageKey, args);
    }

}
