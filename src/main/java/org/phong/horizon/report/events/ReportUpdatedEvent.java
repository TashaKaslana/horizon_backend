package org.phong.horizon.report.events;

import lombok.Getter;
import org.phong.horizon.ably.event.AblyPublishableEvent;
import org.phong.horizon.report.dto.ReportDto;
import org.phong.horizon.report.utils.ReportChannelNames;
import org.springframework.context.ApplicationEvent;

@Getter
public class ReportUpdatedEvent extends ApplicationEvent implements AblyPublishableEvent {
    private final ReportDto report;

    public ReportUpdatedEvent(Object source, ReportDto report) {
        super(source);
        this.report = report;
    }

    @Override
    public String getChannelName() {
        return ReportChannelNames.reports();
    }

    @Override
    public String getEventName() {
        return "report.updated";
    }
}
