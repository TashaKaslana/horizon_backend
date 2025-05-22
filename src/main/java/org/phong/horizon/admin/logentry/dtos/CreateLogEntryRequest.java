package org.phong.horizon.admin.logentry.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.phong.horizon.admin.logentry.enums.LogSeverity;

import java.util.Map;
import java.util.UUID;

@Data
public class CreateLogEntryRequest {
    @NotNull
    private LogSeverity severity;

    @NotBlank
    private String message;

    @NotBlank
    private String source;

    private UUID userId;

    private Map<String, Object> context;
}

