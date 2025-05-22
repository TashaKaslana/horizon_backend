package org.phong.horizon.report.dto;

import lombok.Data;
import org.phong.horizon.report.enums.ModerationItemType;
import org.phong.horizon.report.enums.ModerationStatus;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
public class ReportDto {
    private UUID id;
    private String reason;
    private String moderatorNotes;
    private ModerationStatus status;
    private ModerationItemType itemType;
    private UUID postId;
    private UUID commentId;
    private UUID reportedUserId;
    private UUID reporterId;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}

