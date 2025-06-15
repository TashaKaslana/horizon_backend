package org.phong.horizon.post.subdomain.category.events;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.phong.horizon.ably.event.AblyPublishableEvent;
import org.phong.horizon.post.subdomain.category.utils.PostCategoryChannelNames;
import org.springframework.context.ApplicationEvent;

import java.util.List;
import java.util.UUID;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BulkPostCategoriesDeletedEvent extends ApplicationEvent implements AblyPublishableEvent {
    List<UUID> postCategoryIds;

    public BulkPostCategoriesDeletedEvent(Object source, List<UUID> postCategoryIds) {
        super(source);
        this.postCategoryIds = postCategoryIds;
    }

    @Override
    public String getChannelName() {
        return PostCategoryChannelNames.postCategories();
    }

    @Override
    public String getEventName() {
        return "post.categories.bulk.deleted";
    }
}
