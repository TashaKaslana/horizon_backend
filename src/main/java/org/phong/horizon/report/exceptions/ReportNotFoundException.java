package org.phong.horizon.report.exceptions;

import org.phong.horizon.report.enums.ReportErrorCode;
import java.util.UUID;

public class ReportNotFoundException extends ReportException {
    public ReportNotFoundException(UUID reportId) {
        super(String.format(ReportErrorCode.REPORT_NOT_FOUND.getMessage(), reportId));
    }
}

