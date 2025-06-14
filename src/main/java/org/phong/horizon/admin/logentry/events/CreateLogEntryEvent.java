package org.phong.horizon.admin.logentry.events;

import lombok.Getter;
import org.phong.horizon.ably.event.AblyPublishableEvent;
import org.phong.horizon.admin.logentry.dtos.CreateLogEntryRequest;
import org.phong.horizon.admin.logentry.utils.LogEntryChannelNames;
import org.springframework.context.ApplicationEvent;

@Getter
public class CreateLogEntryEvent extends ApplicationEvent implements AblyPublishableEvent {
    private final CreateLogEntryRequest createLogEntryRequest;

    public CreateLogEntryEvent(Object source, CreateLogEntryRequest createLogEntryRequest) {
        super(source);
        this.createLogEntryRequest = createLogEntryRequest;
    }

    @Override
    public String getChannelName() {
        return LogEntryChannelNames.adminLogs();
    }

    @Override
    public String getEventName() {
        return "admin.log.created";
    }
}

