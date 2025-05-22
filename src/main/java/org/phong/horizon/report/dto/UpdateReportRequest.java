package org.phong.horizon.report.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.phong.horizon.report.enums.ModerationStatus;

@Data
public class UpdateReportRequest {
    @NotNull(message = "Status cannot be null")
    private ModerationStatus status;
    private String moderatorNotes;
}

