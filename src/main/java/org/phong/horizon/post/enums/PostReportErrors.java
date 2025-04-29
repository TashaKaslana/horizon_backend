package org.phong.horizon.post.enums;

import lombok.Getter;

@Getter
public enum PostReportErrors {
    POST_REPORT_NOT_FOUND("Post report not found"),
    REPORT_NOT_FOUND("Report not found");

    private final String message;

    PostReportErrors(String message) {
        this.message = message;
    }

}
