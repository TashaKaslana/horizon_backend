package org.phong.horizon.follow.utils;

import java.util.UUID;

public final class FollowChannelNames {
    private FollowChannelNames() {}

    /**
     * Channel for events when a user gains or loses followers
     */
    public static String userFollowers(UUID userId) {
        return "followers." + userId;
    }
}
