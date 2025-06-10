package org.phong.horizon.report.dto;

import java.util.List;
import java.util.UUID;

public record BulkReportDeleteRequest(List<UUID> reportIds) {
}
