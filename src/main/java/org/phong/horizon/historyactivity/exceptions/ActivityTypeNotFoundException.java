package org.phong.horizon.historyactivity.exceptions;

import org.phong.horizon.historyactivity.enums.HistoryActivityBusinessError;

public class ActivityTypeNotFoundException extends RuntimeException {
    public ActivityTypeNotFoundException(HistoryActivityBusinessError error, String activityTypeCode) {
        super(error.getMessage() + '\n' + String.format("Activity type with code %s not found", activityTypeCode));
    }
}
