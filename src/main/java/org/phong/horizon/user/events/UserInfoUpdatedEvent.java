package org.phong.horizon.user.events;

import lombok.Getter;
import org.phong.horizon.ably.event.AblyPublishableEvent;
import org.phong.horizon.core.dtos.FieldValueChange;
import org.phong.horizon.user.utils.UserChannelNames;
import org.springframework.context.ApplicationEvent;

import java.util.Map;
import java.util.UUID;

@Getter
public class UserInfoUpdatedEvent extends ApplicationEvent implements AblyPublishableEvent {
    private final UUID userId;
    private final String username;
    private final String email;
    private final Map<String, FieldValueChange> additionalInfo;
    private final String userAgent;
    private final String clientIp;

    public UserInfoUpdatedEvent(Object source,
                                UUID userId,
                                String username,
                                String email,
                                Map<String, FieldValueChange> additionalInfo, String userAgent, String clientIp) {
        super(source);
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.additionalInfo = additionalInfo;
        this.userAgent = userAgent;
        this.clientIp = clientIp;
    }

    @Override
    public String getChannelName() {
        return UserChannelNames.user(userId);
    }

    @Override
    public String getEventName() {
        return "user.info.updated";
    }
}
