package org.phong.horizon.post.exceptions;

public class PostWithAssetAlreadyExistException extends RuntimeException {
    public PostWithAssetAlreadyExistException(String message) {
        super(message);
    }
}
