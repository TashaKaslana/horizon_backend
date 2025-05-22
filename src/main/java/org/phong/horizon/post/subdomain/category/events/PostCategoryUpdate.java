package org.phong.horizon.post.subdomain.category.events;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.phong.horizon.core.dtos.FieldValueChange;
import org.springframework.context.ApplicationEvent;

import java.util.Map;
import java.util.UUID;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PostCategoryUpdate extends ApplicationEvent {
    String categoryName;
    Map<String, FieldValueChange> additionalInfo;
    String userAgent;
    String clientIp;
    UUID userId;

    public PostCategoryUpdate(Object source, String categoryName, Map<String, FieldValueChange> additionalInfo,
                              String userAgent, String clientIp, UUID userId) {
        super(source);
        this.categoryName = categoryName;
        this.additionalInfo = additionalInfo;
        this.userAgent = userAgent;
        this.clientIp = clientIp;
        this.userId = userId;
    }
}
