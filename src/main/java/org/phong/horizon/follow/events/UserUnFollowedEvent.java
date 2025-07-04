package org.phong.horizon.follow.events;

import lombok.Getter;
import org.phong.horizon.ably.event.AblyPublishableEvent;
import org.phong.horizon.follow.utils.FollowChannelNames;
import org.springframework.context.ApplicationEvent;

import java.util.UUID;

//websocket later
@Getter
public class UserUnFollowedEvent extends ApplicationEvent implements AblyPublishableEvent {
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

    @Override
    public String getChannelName() {
        return FollowChannelNames.userFollowers(followedUserId);
    }

    @Override
    public String getEventName() {
        return "user.unfollowed";
    }
}
