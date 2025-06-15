package org.phong.horizon.report.events;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.phong.horizon.ably.event.AblyPublishableEvent;
import org.phong.horizon.report.enums.ModerationStatus;
import org.springframework.context.ApplicationEvent;

import java.util.Set;
import java.util.UUID;

/**
 * Event fired when multiple reports are updated in bulk
 */
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BulkReportsUpdatedEvent extends ApplicationEvent implements AblyPublishableEvent {
    Set<UUID> reportIds;
    ModerationStatus status;
    String moderatorNotes;

    public BulkReportsUpdatedEvent(Object source, Set<UUID> reportIds, ModerationStatus status, String moderatorNotes) {
        super(source);
        this.reportIds = reportIds;
        this.status = status;
        this.moderatorNotes = moderatorNotes;
    }

    @Override
    public String getChannelName() {
        return "reports";
    }

    @Override
    public String getEventName() {
        return "reports.bulk.updated";
    }
}
