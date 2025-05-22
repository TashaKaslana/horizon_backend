package org.phong.horizon.core.services;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flywaydb.core.api.callback.Callback;
import org.flywaydb.core.api.callback.Context;
import org.flywaydb.core.api.callback.Event;
import org.phong.horizon.admin.logentry.dtos.CreateLogEntryRequest;
import org.phong.horizon.admin.logentry.enums.LogSeverity;
import org.phong.horizon.admin.logentry.services.LogService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@AllArgsConstructor
@Lazy
public class FlywayLoggingCallback implements Callback {
    private final LogService logService;

    @Bean
    public Callback flywayLoggingCallback(LogService logService) {
        return new FlywayLoggingCallback(logService);
    }

    @Override
    public boolean canHandleInTransaction(Event event, Context context) {
        return false;
    }

    @Override
    public boolean supports(Event event, Context context) {
        return event == Event.AFTER_EACH_MIGRATE ||
                event == Event.AFTER_EACH_MIGRATE_ERROR ||
                event == Event.AFTER_MIGRATE ||
                event == Event.AFTER_MIGRATE_ERROR;
    }

    @Override
    public String getCallbackName() {
        return "HorizonFlywayLoggingCallback";
    }

    @Override
    public void handle(Event event, Context context) {
        CreateLogEntryRequest logEntryRequest = new CreateLogEntryRequest();
        logEntryRequest.setSource("Flyway");

        Map<String, Object> logContext = new HashMap<>();
        logContext.put("flywayEvent", event.getId());

        if (context.getMigrationInfo() != null) {
            logContext.put("migrationVersion", context.getMigrationInfo().getVersion() != null ? context.getMigrationInfo().getVersion().toString() : "N/A");
            logContext.put("migrationDescription", context.getMigrationInfo().getDescription());
            logContext.put("script", context.getMigrationInfo().getScript());
            logContext.put("executionTime", context.getMigrationInfo().getExecutionTime());
        }

        if (context.getStatement() != null) {
            logContext.put("statement", context.getStatement());
        }

        String message;

        if (event == Event.AFTER_EACH_MIGRATE || event == Event.AFTER_MIGRATE) {
            logEntryRequest.setSeverity(LogSeverity.INFO);
            message = "Flyway migration successful: " + event.getId();
            if (context.getMigrationInfo() != null) {
                message += " - " + context.getMigrationInfo().getVersion() + " (" + context.getMigrationInfo().getDescription() + ")";
            }

            logEntryRequest.setMessage(message);
            log.info(message);
        } else if (event == Event.AFTER_EACH_MIGRATE_ERROR || event == Event.AFTER_MIGRATE_ERROR) {
            logEntryRequest.setSeverity(LogSeverity.ERROR);
            String errorMessage = "Flyway migration failed: " + event.getId();
            if (context.getMigrationInfo() != null) {
                errorMessage += " - " + context.getMigrationInfo().getVersion() + " (" + context.getMigrationInfo().getDescription() + ")";
            }

            logEntryRequest.setMessage(errorMessage);
            log.error(errorMessage);
        }

        logEntryRequest.setContext(logContext);

        try {
            logService.createLogEntry(logEntryRequest);
        } catch (Exception e) {
            log.error("Failed to log Flyway migration event to LogService: {}", e.getMessage(), e);
        }
    }
}
