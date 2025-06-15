package org.phong.horizon.report.events;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.phong.horizon.ably.event.AblyPublishableEvent;
import org.springframework.context.ApplicationEvent;

import java.util.List;
import java.util.UUID;

/**
 * Event fired when multiple reports are deleted in bulk
 */
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BulkReportsDeletedEvent extends ApplicationEvent implements AblyPublishableEvent {
    List<UUID> reportIds;

    public BulkReportsDeletedEvent(Object source, List<UUID> reportIds) {
        super(source);
        this.reportIds = reportIds;
    }

    @Override
    public String getChannelName() {
        return "reports";
    }

    @Override
    public String getEventName() {
        return "reports.bulk.deleted";
    }
}
