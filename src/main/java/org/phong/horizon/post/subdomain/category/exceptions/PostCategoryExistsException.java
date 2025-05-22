package org.phong.horizon.post.subdomain.category.exceptions;

public class PostCategoryExistsException extends RuntimeException {
    public PostCategoryExistsException(String message) {
        super(message);
    }
}
