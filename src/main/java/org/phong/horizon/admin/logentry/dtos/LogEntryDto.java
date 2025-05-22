package org.phong.horizon.admin.logentry.dtos;

import lombok.Data;
import org.phong.horizon.admin.logentry.enums.LogSeverity;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

@Data
public class LogEntryDto {
    private UUID id;
    private OffsetDateTime timestamp;
    private LogSeverity severity;
    private String message;
    private String source;
    private UUID userId;
    private Map<String, Object> context;
    private OffsetDateTime createdAt;
}

