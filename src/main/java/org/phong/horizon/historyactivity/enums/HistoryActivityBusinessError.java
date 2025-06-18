package org.phong.horizon.historyactivity.enums;

import lombok.Getter;
import org.phong.horizon.core.services.LocalizationProvider;

@Getter
public enum HistoryActivityBusinessError {
    ACTIVITY_TYPE_NOT_FOUND("history_activity.error.type_not_found"),
    ACTIVITY_TYPE_CODE_NOT_FOUND("history_activity.error.code_not_found"),
    ACTIVITY_TYPE_CODE_INVALID("history_activity.error.code_invalid"),
    ACTIVITY_TYPE_CODE_DUPLICATE("history_activity.error.code_duplicate"),
    ACTIVITY_TYPE_NAME_DUPLICATE("history_activity.error.name_duplicate");

    private final String messageKey;

    HistoryActivityBusinessError(String messageKey) {
        this.messageKey = messageKey;
    }

    public String getMessage(Object... args) {
        return LocalizationProvider.getMessage(this.messageKey, args);
    }
}
