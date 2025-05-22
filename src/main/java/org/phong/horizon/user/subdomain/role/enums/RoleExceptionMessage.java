package org.phong.horizon.user.subdomain.role.enums;

import lombok.Getter;

@Getter
public enum RoleExceptionMessage {
    ROLE_NOT_FOUND("Role not found with ID: %s"),
    ROLE_BY_SLUG_NOT_FOUND("Role not found with slug: %s"),
    ROLE_ALREADY_EXISTS_SLUG("Role with slug '%s' already exists."),
    ROLE_ALREADY_EXISTS_NAME("Role with name '%s' already exists."),
    PERMISSION_NOT_FOUND_FOR_ROLE("One or more permissions not found for assignment to role. Invalid IDs: %s"),
    CANNOT_DELETE_ROLE_HAS_USERS("Cannot delete role '%s' as it is currently assigned to users."); // Example

    private final String message;

    RoleExceptionMessage(String message) {
        this.message = message;
    }

    public String format(Object... args) {
        return String.format(message, args);
    }
}

