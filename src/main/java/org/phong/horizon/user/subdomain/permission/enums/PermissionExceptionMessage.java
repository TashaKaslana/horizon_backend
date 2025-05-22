package org.phong.horizon.user.subdomain.permission.enums;

import lombok.Getter;

@Getter
public enum PermissionExceptionMessage {
    PERMISSION_NOT_FOUND("Permission not found with ID: %s"),
    PERMISSION_BY_SLUG_NOT_FOUND("Permission not found with slug: %s"),
    PERMISSION_ALREADY_EXISTS_SLUG("Permission with slug '%s' already exists."),
    PERMISSION_ALREADY_EXISTS_NAME("Permission with name '%s' already exists."),
    PERMISSIONS_NOT_FOUND_BY_IDS("One or more permissions not found. Invalid IDs: %s");

    private final String message;

    PermissionExceptionMessage(String message) {
        this.message = message;
    }

    public String format(Object... args) {
        return String.format(message, args);
    }
}

