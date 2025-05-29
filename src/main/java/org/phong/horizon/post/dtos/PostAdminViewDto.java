package org.phong.horizon.post.dtos;

import org.phong.horizon.core.enums.Visibility;
import org.phong.horizon.post.enums.PostStatus;
import org.phong.horizon.post.infrastructure.persistence.entities.Post;
import org.phong.horizon.post.subdomain.category.dtos.PostCategorySummary;
import org.phong.horizon.storage.dtos.AssetRespond;
import org.phong.horizon.user.dtos.UserSummaryRespond;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * DTO for {@link Post}
 */
public record PostAdminViewDto(
        Instant createdAt,
        Instant updatedAt,
        UUID createdBy,
        UUID updatedBy,
        UUID id,
        UserSummaryRespond user,
        String caption,
        String description,
        Double duration,
        Visibility visibility,
        List<String> tags,
        String videoPlaybackUrl,
        String videoThumbnailUrl,
        AssetRespond videoAsset,
        Boolean isAuthorDeleted,
        PostStatus status,
        long totalViews,
        long totalInteractions,
        PostCategorySummary category) implements Serializable {
}


