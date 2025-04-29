package org.phong.horizon.post.exceptions;

public class PostCategoryNotFoundException extends RuntimeException {
    public PostCategoryNotFoundException(String message) {
        super(message);
    }
}
