package org.phong.horizon.post.utils;

import java.util.UUID;

public final class PostChannelNames {
    private PostChannelNames() {}

    /**
     * Channel for broadcast post creation events
     */
    public static String posts() {
        return "posts";
    }

    /**
     * Channel for events related to a specific post
     */
    public static String post(UUID postId) {
        return "posts." + postId;
    }
}
