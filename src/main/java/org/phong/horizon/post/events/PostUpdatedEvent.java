package org.phong.horizon.post.events;

import lombok.Getter;
import org.phong.horizon.core.dtos.FieldValueChange;
import org.springframework.context.ApplicationEvent;

import java.util.Map;
import java.util.UUID;

@Getter
public class PostUpdatedEvent extends ApplicationEvent {
    private final UUID postId;
    private final UUID userId;
    private final String caption;
    private final String description;
    private final Map<String, FieldValueChange> additionalInfo;
    private final String userAgent;
    private final String clientIp;

    public PostUpdatedEvent(Object source,
                            UUID postId,
                            UUID userId,
                            String caption,
                            String description,
                            Map<String, FieldValueChange> additionalInfo, String userAgent, String clientIp) {
        super(source);
        this.postId = postId;
        this.userId = userId;
        this.caption = caption;
        this.description = description;
        this.additionalInfo = additionalInfo;
        this.userAgent = userAgent;
        this.clientIp = clientIp;
    }
}
