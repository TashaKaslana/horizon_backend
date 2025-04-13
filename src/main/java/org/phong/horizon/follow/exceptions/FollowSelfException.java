package org.phong.horizon.follow.exceptions;

import org.phong.horizon.follow.enums.FollowErrorEnums;

public class FollowSelfException extends RuntimeException {
    public FollowSelfException(FollowErrorEnums message) {
        super(message.getMessage());
    }
}
