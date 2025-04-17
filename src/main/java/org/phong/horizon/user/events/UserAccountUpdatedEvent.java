package org.phong.horizon.user.events;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.phong.horizon.core.dtos.FieldValueChange;
import org.springframework.context.ApplicationEvent;

import java.util.Map;
import java.util.UUID;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Getter
public class UserAccountUpdatedEvent extends ApplicationEvent {
    UUID userId;
    String username;
    String email;
    String profileImage;
    String coverImage;
    String bio;
    Map<String, FieldValueChange> additionalInfo;

    public UserAccountUpdatedEvent(Object source, UUID userId, String username, String email, String profileImage, String coverImage, String bio, Map<String, FieldValueChange> additionalInfo) {
        super(source);
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.profileImage = profileImage;
        this.coverImage = coverImage;
        this.bio = bio;
        this.additionalInfo = additionalInfo;
    }
}
