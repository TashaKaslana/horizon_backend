package org.phong.horizon.report.events;

import lombok.Getter;
import org.phong.horizon.ably.event.AblyPublishableEvent;
import org.phong.horizon.report.utils.ReportChannelNames;
import org.springframework.context.ApplicationEvent;

import java.util.UUID;

@Getter
public class ReportDeletedEvent extends ApplicationEvent implements AblyPublishableEvent {
    private final UUID reportId;

    public ReportDeletedEvent(Object source, UUID reportId) {
        super(source);
        this.reportId = reportId;
    }

    @Override
    public String getChannelName() {
        return ReportChannelNames.report(reportId);
    }

    @Override
    public String getEventName() {
        return "report.deleted";
    }
}
