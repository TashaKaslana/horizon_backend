package org.phong.horizon.report.events;

import lombok.Getter;
import org.phong.horizon.report.enums.ModerationItemType;
import org.springframework.context.ApplicationEvent;

import java.util.UUID;

@Getter
public class ReportCreatedEvent extends ApplicationEvent {
    private final UUID reportId;
    private final UUID reporterId;
    private final UUID reportedItemId; // ID of the post, comment, or user being reported
    private final ModerationItemType itemType;
    private final UUID actualReportedUserId; // User who owns the post/comment, or the user directly reported
    private final String reason;

    public ReportCreatedEvent(Object source, UUID reportId, UUID reporterId, UUID reportedItemId, ModerationItemType itemType, UUID actualReportedUserId, String reason) {
        super(source);
        this.reportId = reportId;
        this.reporterId = reporterId;
        this.reportedItemId = reportedItemId;
        this.itemType = itemType;
        this.actualReportedUserId = actualReportedUserId;
        this.reason = reason;
    }
}

