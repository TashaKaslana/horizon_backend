package org.phong.horizon.follow.events;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.util.UUID;

@Getter
public class UserUnFollowedEvent extends ApplicationEvent {
    private final UUID followerUserId;
    private final UUID followedUserId;
    private final String followerUsername;
    private final String followedUsername;

    public UserUnFollowedEvent(Object object,
                                UUID followerUserId,
                                UUID followedUserId,
                                String followerUsername,
                                String followedUsername) {
        super(object);
        this.followerUserId = followerUserId;
        this.followedUserId = followedUserId;
        this.followerUsername = followerUsername;
        this.followedUsername = followedUsername;
    }
}
