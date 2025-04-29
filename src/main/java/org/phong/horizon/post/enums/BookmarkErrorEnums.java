package org.phong.horizon.post.enums;

import lombok.Getter;

@Getter
public enum BookmarkErrorEnums {
    BOOKMARK_NOT_FOUND("Bookmark not found"),
    BOOKMARK_EXISTS("Bookmark already exists"),
    BOOKMARK_NOT_OWNED("Bookmark not owned by user"),
    BOOKMARK_NOT_SAVED("Bookmark not saved"),
    BOOKMARK_NOT_DELETED("Bookmark not deleted");

    private final String message;

    BookmarkErrorEnums(String message) {
        this.message = message;
    }

}
