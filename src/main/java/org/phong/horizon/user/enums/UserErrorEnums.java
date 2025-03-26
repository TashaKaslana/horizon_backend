package org.phong.horizon.user.enums;

import lombok.Getter;

@Getter
public enum UserErrorEnums {
    USER_NOT_FOUND("User not found"),
    USER_PROFILE_NOT_FOUND("User profile not found"),
    INVALID_SQL_QUERY("Invalid SQL query"),
    PASSWORD_NOT_MATCHING("Passwords do not match"),
    USER_ALREADY_EXISTS("User already exists");

    private final String message;

    UserErrorEnums(String message) {
        this.message = message;
    }
}
