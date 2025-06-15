package org.phong.horizon.follow.events;

import lombok.Getter;
import org.phong.horizon.ably.event.AblyPublishableEvent;
import org.phong.horizon.follow.dtos.FollowOneSideRespond;
import org.phong.horizon.follow.utils.FollowChannelNames;
import org.phong.horizon.notification.dtos.CreateNotificationRequest;
import org.phong.horizon.notification.enums.NotificationType;
import org.phong.horizon.notification.events.NotificationPublishableEvent;
import org.springframework.context.ApplicationEvent;

import java.util.UUID;

@Getter
public class UserFollowedEvent extends ApplicationEvent implements AblyPublishableEvent, NotificationPublishableEvent {
    private final FollowOneSideRespond follower;
    private final UUID followedUserId;

    public UserFollowedEvent(Object source,
                             FollowOneSideRespond follower,
                             UUID followedUserId) {
        super(source);
        this.follower = follower;
        this.followedUserId = followedUserId;
    }

    @Override
    public String getChannelName() {
        return FollowChannelNames.userFollowers(followedUserId);
    }

    @Override
    public String getEventName() {
        return "user.followed";
    }

    @Override
    public CreateNotificationRequest getNotificationRequest() {
        return CreateNotificationRequest.builder()
                .recipientUserId(followedUserId)
                .content("You have a new follower: " + follower.user().username())
                .type(NotificationType.NEW_FOLLOWER)
                .build();
    }
}
