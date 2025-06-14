package org.phong.horizon.user.events;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.phong.horizon.ably.event.AblyPublishableEvent;
import org.phong.horizon.core.dtos.FieldValueChange;
import org.phong.horizon.user.utils.UserChannelNames;
import org.springframework.context.ApplicationEvent;

import java.util.Map;
import java.util.UUID;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Getter
public class UserAccountUpdatedEvent extends ApplicationEvent implements AblyPublishableEvent {
    UUID userId;
    String username;
    String email;
    String profileImage;
    String coverImage;
    String bio;
    Map<String, FieldValueChange> additionalInfo;
    String userAgent;
    String clientIp;

    public UserAccountUpdatedEvent(Object source, UUID userId, String username, String email, String profileImage, String coverImage, String bio, Map<String, FieldValueChange> additionalInfo, String userAgent, String clientIp) {
        super(source);
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.profileImage = profileImage;
        this.coverImage = coverImage;
        this.bio = bio;
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
        return "user.account.updated";
    }
}
