package org.phong.horizon.user.utils;

import java.util.UUID;

public final class UserChannelNames {
    private UserChannelNames() {}

    /**
     * Channel for user creation events. Represents all users collectively.
     */
    public static String users() {
        return "users";
    }

    /**
     * Channel for events scoped to a specific user.
     */
    public static String user(UUID userId) {
        return "users." + userId;
    }
}
