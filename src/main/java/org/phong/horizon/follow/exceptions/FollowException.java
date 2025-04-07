package org.phong.horizon.follow.exceptions;

import org.phong.horizon.follow.enums.FollowErrorEnums;

public class FollowException extends RuntimeException {
    public FollowException(FollowErrorEnums error) {
        super(error.getMessage());
    }
}
