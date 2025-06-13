package org.phong.horizon.comment.utils;

import java.util.UUID;

public class CommentChannelNames {
    public static String comment(UUID commentId) {
        return "comment." + commentId;
    }

    public static String commentsUnderPost(UUID postId) {
        return "comments." + postId;
    }
}
