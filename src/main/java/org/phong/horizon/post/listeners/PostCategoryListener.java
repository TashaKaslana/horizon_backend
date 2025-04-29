package org.phong.horizon.post.listeners;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.phong.horizon.core.enums.SystemCategory;
import org.phong.horizon.historyactivity.dtos.CreateHistoryActivity;
import org.phong.horizon.historyactivity.enums.ActivityTypeCode;
import org.phong.horizon.historyactivity.events.CreateHistoryLogEvent;
import org.phong.horizon.post.events.PostCategoryUpdate;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.Map;

@Component
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PostCategoryListener {
    ApplicationEventPublisher eventPublisher;

    @EventListener
    @Async
    @TransactionalEventListener
    public void onApplicationEvent(PostCategoryUpdate event) {
        eventPublisher.publishEvent(new CreateHistoryLogEvent(
                this,
                new CreateHistoryActivity(
                        ActivityTypeCode.POST_CATEGORY_UPDATE,
                        "Post Category Updated",
                        Map.of("diffChange", event.getAdditionalInfo()),
                        event.getUserId(),
                        SystemCategory.POST.getName(),
                        event.getUserId(),
                        event.getUserAgent(),
                        event.getClientIp()
                )
        ));
    }
}
