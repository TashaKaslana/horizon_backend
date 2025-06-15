package org.phong.horizon.post.subdomain.tag.events;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.phong.horizon.ably.event.AblyPublishableEvent;
import org.springframework.context.ApplicationEvent;

import java.util.UUID;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TagDeletedEvent extends ApplicationEvent implements AblyPublishableEvent {
    UUID tagId;
    String tagName;

    public TagDeletedEvent(Object source, UUID tagId, String tagName) {
        super(source);
        this.tagId = tagId;
        this.tagName = tagName;
    }

    @Override
    public String getChannelName() {
        return "post-tags";
    }

    @Override
    public String getEventName() {
        return "post.tag.deleted";
    }
}
