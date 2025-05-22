package org.phong.horizon.post.subdomain.category.exceptions;

public class PostCategoryNotFoundException extends RuntimeException {
    public PostCategoryNotFoundException(String message) {
        super(message);
    }
}
