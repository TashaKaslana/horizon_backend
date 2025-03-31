package org.phong.horizon.post.exceptions;

public class PostPermissionDenialException extends RuntimeException {
    public PostPermissionDenialException(String message) {
        super(message);
    }
}
