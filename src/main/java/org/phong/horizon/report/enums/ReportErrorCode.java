package org.phong.horizon.report.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ReportErrorCode {
    REPORT_NOT_FOUND("Report not found with ID: %s"),
    INVALID_ITEM_TYPE("Invalid item type specified for the report."),
    MISSING_POST_ID("Post ID is required when item type is POST."),
    MISSING_COMMENT_ID("Comment ID is required when item type is COMMENT."),
    MISSING_USER_ID("Reported User ID is required when item type is USER."),
    CANNOT_REPORT_SELF("Users cannot report themselves."),
    INVALID_STATUS_TRANSITION("Invalid status transition from %s to %s.");

    private final String message;
}

