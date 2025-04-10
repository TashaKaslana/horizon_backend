package org.phong.horizon.historyactivity.dtos;

import org.phong.horizon.historyactivity.enums.ActivityTypeCode;

import java.io.Serializable;
import java.util.Map;
import java.util.UUID;

/**
 * DTO for {@link org.phong.horizon.historyactivity.infrstructure.persistence.enitities.HistoryActivity}
 */
public record CreateHistoryActivity(ActivityTypeCode activityTypeCode, String activityDescription,
                                    Map<String, Object> activityDetail, UUID targetId, String targetType,
                                    UUID userId, String userAgent, String ipAddress) implements Serializable {
}