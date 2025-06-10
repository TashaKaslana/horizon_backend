package org.phong.horizon.report.dto;

import org.phong.horizon.report.enums.ModerationStatus;

import java.util.List;
import java.util.UUID;

public record BulkReportUpdateRequest(List<UUID> reportIds, ModerationStatus status, String moderatorNotes) {
}
