package org.phong.horizon.report.utils;

public final class ReportChannelNames {
    private ReportChannelNames() {}

    /**
     * Global channel for new reports
     */
    public static String reports() {
        return "reports";
    }

    /**
     * Channel for events related to a specific report
     */
    public static String report(java.util.UUID reportId) {
        return "reports." + reportId;
    }
}
