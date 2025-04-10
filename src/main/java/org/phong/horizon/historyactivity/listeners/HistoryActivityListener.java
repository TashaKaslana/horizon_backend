package org.phong.horizon.historyactivity.listeners;

import lombok.AllArgsConstructor;
import org.phong.horizon.historyactivity.events.CreateHistoryLogEvent;
import org.phong.horizon.historyactivity.services.ActivityLoggingService;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@AllArgsConstructor
@Component
public class HistoryActivityListener {
    private final ActivityLoggingService activityLoggingService;

    @EventListener
    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onCreateHistoryLog(CreateHistoryLogEvent event) {
        activityLoggingService.logActivity(event.getRequest());
    }
}
