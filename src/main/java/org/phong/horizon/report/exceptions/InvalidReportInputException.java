package org.phong.horizon.report.exceptions;

import org.phong.horizon.report.enums.ReportErrorCode;

public class InvalidReportInputException extends ReportException {
    public InvalidReportInputException(ReportErrorCode errorCode) {
        super(errorCode.getMessage());
    }
    public InvalidReportInputException(String message) {
        super(message);
    }
}

