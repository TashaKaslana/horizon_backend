package org.phong.horizon.admin.logentry.exceptions;

public class LogEntryNotFoundException extends RuntimeException {
    public LogEntryNotFoundException(String message) {
        super(message);
    }
}

