package org.phong.horizon.post.exceptions;

public class PostCategoryExistsException extends RuntimeException {
    public PostCategoryExistsException(String message) {
        super(message);
    }
}
