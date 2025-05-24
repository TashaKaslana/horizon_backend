package org.phong.horizon.core.listeners;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.phong.horizon.admin.logentry.dtos.CreateLogEntryRequest;
import org.phong.horizon.admin.logentry.enums.LogSeverity;
import org.phong.horizon.admin.logentry.events.CreateLogEntryEvent;
import org.springframework.boot.SpringBootVersion;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class ApplicationStartupListener {

    private final ApplicationEventPublisher eventPublisher;

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady(ApplicationReadyEvent event) {
        log.info("Application is ready. Publishing startup log event...");

        CreateLogEntryRequest logRequest = new CreateLogEntryRequest();
        logRequest.setSeverity(LogSeverity.INFO);
        logRequest.setMessage("Server initialized successfully. Application is ready to service requests.");
        logRequest.setSource("ApplicationStartup");
        logRequest.setUserId(null);

        Map<String, Object> context = new HashMap<>();
        context.put("springBootVersion", SpringBootVersion.getVersion());
        context.put("activeProfiles", String.join(", ", event.getApplicationContext().getEnvironment().getActiveProfiles()));
        logRequest.setContext(context);

        try {
            eventPublisher.publishEvent(new CreateLogEntryEvent(this, logRequest));
            log.info("Application startup log event published successfully.");
        } catch (Exception e) {
            log.error("Failed to publish application startup log event: {}", e.getMessage(), e);
        }
    }
}

