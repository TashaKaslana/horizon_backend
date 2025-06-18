package org.phong.horizon.post.enums;

import lombok.Getter;
import org.phong.horizon.core.services.LocalizationProvider;

@Getter
public enum BookmarkErrorEnums {
    BOOKMARK_NOT_FOUND("bookmark.error.not_found"),
    BOOKMARK_EXISTS("bookmark.error.exists"),
    BOOKMARK_NOT_OWNED("bookmark.error.not_owned"),
    BOOKMARK_NOT_SAVED("bookmark.error.not_saved"),
    BOOKMARK_NOT_DELETED("bookmark.error.not_deleted");

    private final String messageKey;

    BookmarkErrorEnums(String messageKey) {
        this.messageKey = messageKey;
    }

    public String getMessage(Object... args) {
        return LocalizationProvider.getMessage(this.messageKey, args);
    }

}
