package org.phong.horizon.post.subdomain.tag.events;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.phong.horizon.ably.event.AblyPublishableEvent;
import org.phong.horizon.post.subdomain.tag.dto.TagResponse;
import org.springframework.context.ApplicationEvent;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TagCreatedEvent extends ApplicationEvent implements AblyPublishableEvent {
    TagResponse tag;

    public TagCreatedEvent(Object source, TagResponse tag) {
        super(source);
        this.tag = tag;
    }

    @Override
    public String getChannelName() {
        return "post-tags";
    }

    @Override
    public String getEventName() {
        return "post.tag.created";
    }
}
