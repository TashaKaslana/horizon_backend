package org.phong.horizon.admin.logentry.dtos;

import lombok.Data;
import org.phong.horizon.admin.logentry.enums.LogSeverity;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Data
public class LogSearchCriteriaDto {
    private List<LogSeverity> severities;
    private String messageContains;
    private String source;
    private UUID userId;
    private OffsetDateTime timestampAfter;
    private OffsetDateTime timestampBefore;
}

