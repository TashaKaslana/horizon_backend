package org.phong.horizon.admin.logentry.events;

import lombok.Getter;
import org.phong.horizon.admin.logentry.dtos.CreateLogEntryRequest;
import org.springframework.context.ApplicationEvent;

@Getter
public class CreateLogEntryEvent extends ApplicationEvent {
    private final CreateLogEntryRequest createLogEntryRequest;

    public CreateLogEntryEvent(Object source, CreateLogEntryRequest createLogEntryRequest) {
        super(source);
        this.createLogEntryRequest = createLogEntryRequest;
    }
}

