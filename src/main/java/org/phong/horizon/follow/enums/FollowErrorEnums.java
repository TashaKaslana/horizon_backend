package org.phong.horizon.follow.enums;

import lombok.Getter;

@Getter
public enum FollowErrorEnums {
    FOLLOW_NOT_FOUND("Follow not found"),
    FOLLOW_ALREADY_EXISTS("Follow already exists"),
    FOLLOWER_NOT_FOUND("Follower not found"),
    FOLLOWING_NOT_FOUND("Following not found"),
    INVALID_FOLLOW_REQUEST("Invalid follow request"),
    UNABLE_TO_FOLLOW_SELF("Unable to follow yourself"),
    UNABLE_TO_UNFOLLOW_SELF("Unable to unfollow yourself"),
    NOT_FOLLOWING("Not following"),;

    private final String message;

    FollowErrorEnums(String message) {
        this.message = message;
    }

}
