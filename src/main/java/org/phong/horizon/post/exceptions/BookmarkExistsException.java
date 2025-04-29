package org.phong.horizon.post.exceptions;

public class BookmarkExistsException extends RuntimeException {
    public BookmarkExistsException(String message) {
        super(message);
    }
}
