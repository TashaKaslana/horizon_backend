package org.phong.horizon.historyactivity.events;

import lombok.Getter;
import org.phong.horizon.ably.event.AblyPublishableEvent;
import org.phong.horizon.historyactivity.dtos.CreateHistoryActivity;
import org.phong.horizon.historyactivity.utils.HistoryChannelNames;
import org.springframework.context.ApplicationEvent;

@Getter
public class CreateHistoryLogEvent extends ApplicationEvent implements AblyPublishableEvent {
    private final CreateHistoryActivity request;

    public CreateHistoryLogEvent(Object source, CreateHistoryActivity request) {
        super(source);
        this.request = request;
    }

    @Override
    public String getChannelName() {
        return HistoryChannelNames.userHistory(request.userId());
    }

    @Override
    public String getEventName() {
        return "history.created";
    }
}
