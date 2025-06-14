package org.phong.horizon.historyactivity.utils;

import java.util.UUID;

public final class HistoryChannelNames {
    private HistoryChannelNames() {}

    /**
     * Channel for a user's history log events
     */
    public static String userHistory(UUID userId) {
        return "history." + userId;
    }
}
