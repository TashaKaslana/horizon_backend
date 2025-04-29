package org.phong.horizon.post.exceptions;

public class BookmarkNotFoundException extends RuntimeException {
    public BookmarkNotFoundException(String message) {
        super(message);
    }
}
