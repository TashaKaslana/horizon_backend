package org.phong.horizon.historyactivity.enums;

import lombok.Getter;

@Getter
public enum HistoryActivityBusinessError {
    ACTIVITY_TYPE_NOT_FOUND("Activity type not found"),
    ACTIVITY_TYPE_CODE_NOT_FOUND("Activity type code not found"),
    ACTIVITY_TYPE_CODE_INVALID("Activity type code is invalid"),
    ACTIVITY_TYPE_CODE_DUPLICATE("Activity type code is duplicate"),
    ACTIVITY_TYPE_NAME_DUPLICATE("Activity type name is duplicate");

    private final String message;

    HistoryActivityBusinessError( String message) {
        this.message = message;
    }
}
