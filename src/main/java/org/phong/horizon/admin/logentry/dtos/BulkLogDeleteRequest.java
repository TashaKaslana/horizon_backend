package org.phong.horizon.admin.logentry.dtos;

import java.util.List;
import java.util.UUID;

public record BulkLogDeleteRequest(List<UUID> logIds) {
}
