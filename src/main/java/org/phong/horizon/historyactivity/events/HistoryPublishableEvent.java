package org.phong.horizon.historyactivity.events;

import org.phong.horizon.historyactivity.dtos.CreateHistoryActivity;

public interface HistoryPublishableEvent {
    CreateHistoryActivity getHistoryRequest();
}
