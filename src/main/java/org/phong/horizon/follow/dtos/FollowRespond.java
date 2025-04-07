package org.phong.horizon.follow.dtos;

import org.phong.horizon.user.dtos.UserSummaryRespond;

import java.io.Serializable;
import java.time.Instant;

/**
 * DTO for {@link org.phong.horizon.follow.infrastructure.persistence.entities.Follow}
 */
public record FollowRespond(UserSummaryRespond follower, UserSummaryRespond following,
                            Instant createdAt) implements Serializable {
}