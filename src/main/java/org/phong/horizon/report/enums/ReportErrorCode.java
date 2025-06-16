package org.phong.horizon.report.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ReportErrorCode {
    REPORT_NOT_FOUND("report.error.not_found"),
    INVALID_ITEM_TYPE("report.error.invalid_item_type"),
    MISSING_POST_ID("report.error.missing_post_id"),
    MISSING_COMMENT_ID("report.error.missing_comment_id"),
    MISSING_USER_ID("report.error.missing_user_id"),
    CANNOT_REPORT_SELF("report.error.cannot_report_self"),
    INVALID_STATUS_TRANSITION("report.error.invalid_status_transition");

    private final String messageKey;

    public String getMessage(Object... args) {
        return org.phong.horizon.core.config.LocalizationProvider.getMessage(this.messageKey, args);
    }
}

