package org.phong.horizon.follow.dtos;

import org.phong.horizon.user.dtos.UserIntroduction;
import org.phong.horizon.user.dtos.UserSummaryRespond;

import java.io.Serializable;
import java.time.Instant;

/**
 * DTO for {@link org.phong.horizon.follow.infrastructure.persistence.entities.Follow}
 */
public record FollowOneSideRespond(UserIntroduction user, Instant createdAt) implements Serializable {
}