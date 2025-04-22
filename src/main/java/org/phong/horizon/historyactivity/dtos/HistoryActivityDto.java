package org.phong.horizon.historyactivity.dtos;

import org.phong.horizon.historyactivity.infrstructure.persistence.enitities.ActivityType;
import org.phong.horizon.user.dtos.UserSummaryRespond;

import java.io.Serializable;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

/**
 * DTO for {@link org.phong.horizon.historyactivity.infrstructure.persistence.enitities.HistoryActivity}
 */
public record HistoryActivityDto(UUID id, ActivityType activityType, String activityDescription,
                                 Map<String, Object> activityDetail, UUID targetId, String targetType,
                                 UserSummaryRespond user, String userAgent, String ipAddress,
                                 Instant createdAt) implements Serializable {
}