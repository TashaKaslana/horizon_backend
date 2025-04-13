package org.phong.horizon.follow.exceptions;

import org.phong.horizon.follow.enums.FollowErrorEnums;

public class FollowNotFoundException extends RuntimeException {
    public FollowNotFoundException(FollowErrorEnums message) {
        super(message.getMessage());
    }
}
