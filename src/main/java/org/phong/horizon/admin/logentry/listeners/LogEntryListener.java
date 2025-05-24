package org.phong.horizon.admin.logentry.listeners;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.phong.horizon.admin.logentry.events.CreateLogEntryEvent;
import org.phong.horizon.admin.logentry.services.LogService;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
public class LogEntryListener {

    private final LogService logService;

    @EventListener
    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleCreateLogEntry(CreateLogEntryEvent event) {
        try {
            log.debug("Handling CreateLogEntryEvent from source: {} for user: {}",
                      event.getCreateLogEntryRequest().getSource(),
                      event.getCreateLogEntryRequest().getUserId());
            logService.createLogEntry(event.getCreateLogEntryRequest());
            log.info("Log entry created successfully from source: {} for user: {}",
                     event.getCreateLogEntryRequest().getSource(),
                     event.getCreateLogEntryRequest().getUserId());
        } catch (Exception e) {
            log.error("Failed to create log entry from source: {} for user: {}. Error: {}",
                      event.getCreateLogEntryRequest().getSource(),
                      event.getCreateLogEntryRequest().getUserId(),
                      e.getMessage(), e);
        }
    }
}

