package org.phong.horizon.storage.exceptions;

public class CloudinaryException extends RuntimeException {
    public CloudinaryException(String message, Throwable cause) {
        // Constructor if public ID isn't applicable or known
        super(message, cause);
    }
}
