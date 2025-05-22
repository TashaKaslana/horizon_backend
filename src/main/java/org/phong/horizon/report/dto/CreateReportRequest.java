package org.phong.horizon.report.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.phong.horizon.report.enums.ModerationItemType;

import java.util.UUID;

@Data
public class CreateReportRequest {
    @NotBlank(message = "Reason cannot be blank")
    private String reason;

    @NotNull(message = "Item type cannot be null")
    private ModerationItemType itemType;

    private UUID postId;
    private UUID commentId;
    private UUID reportedUserId;
}

