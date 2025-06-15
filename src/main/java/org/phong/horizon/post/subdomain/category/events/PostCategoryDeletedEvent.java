package org.phong.horizon.post.subdomain.category.events;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.phong.horizon.ably.event.AblyPublishableEvent;
import org.phong.horizon.post.subdomain.category.utils.PostCategoryChannelNames;
import org.springframework.context.ApplicationEvent;

import java.util.UUID;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PostCategoryDeletedEvent extends ApplicationEvent implements AblyPublishableEvent {
    UUID postCategoryId;
    String categoryName;
    String userAgent;
    String clientIp;
    UUID userId;

    public PostCategoryDeletedEvent(Object source, UUID postCategoryId, String categoryName,
                                   String userAgent, String clientIp, UUID userId) {
        super(source);
        this.postCategoryId = postCategoryId;
        this.categoryName = categoryName;
        this.userAgent = userAgent;
        this.clientIp = clientIp;
        this.userId = userId;
    }

    @Override
    public String getChannelName() {
        return PostCategoryChannelNames.postCategories();
    }

    @Override
    public String getEventName() {
        return "post.category.deleted";
    }
}
