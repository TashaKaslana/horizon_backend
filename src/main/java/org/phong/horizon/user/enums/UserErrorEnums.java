package org.phong.horizon.user.enums;

import lombok.Getter;

@Getter
public enum UserErrorEnums {
    USER_NOT_FOUND("user.error.not_found"),
    USER_PROFILE_NOT_FOUND("user.error.profile_not_found"),
    INVALID_SQL_QUERY("user.error.invalid_sql_query"),
    PASSWORD_NOT_MATCHING("user.error.password_not_matching"),
    USER_ALREADY_EXISTS("user.error.user_already_exists");

    private final String messageKey;

    UserErrorEnums(String messageKey) {
        this.messageKey = messageKey;
    }

    public String getMessage(Object... args) {
        return org.phong.horizon.core.config.LocalizationProvider.getMessage(this.messageKey, args);
    }
}
