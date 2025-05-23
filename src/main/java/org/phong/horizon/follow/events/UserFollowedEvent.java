package org.phong.horizon.follow.events;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.util.UUID;

@Getter
public class UserFollowedEvent extends ApplicationEvent {
    private final String followerUsername;
    private final String followedUsername;
    private final UUID followedUserId;
    private final UUID followerUserId;

    public UserFollowedEvent(Object object,
                             String followerUsername,
                             String followedUsername,
                             UUID followedUserId,
                             UUID followerUserId) {
        super(object);
        this.followerUsername = followerUsername;
        this.followedUsername = followedUsername;
        this.followedUserId = followedUserId;
        this.followerUserId = followerUserId;
    }
}
