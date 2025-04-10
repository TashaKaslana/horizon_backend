package org.phong.horizon.historyactivity.events;

import lombok.Getter;
import org.phong.horizon.historyactivity.dtos.CreateHistoryActivity;
import org.springframework.context.ApplicationEvent;

@Getter
public class CreateHistoryLogEvent extends ApplicationEvent {
    private final CreateHistoryActivity request;

    public CreateHistoryLogEvent(Object source, CreateHistoryActivity request) {
        super(source);
        this.request = request;
    }
}
