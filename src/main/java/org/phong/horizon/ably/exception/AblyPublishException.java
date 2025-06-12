package org.phong.horizon.ably.exception;

public class AblyPublishException extends RuntimeException {

    public AblyPublishException(String message, Throwable cause) {
        super(message, cause);
    }

    public AblyPublishException(String message) {
        super(message);
    }
}

