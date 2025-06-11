package org.phong.horizon.post.dtos;

import jakarta.validation.constraints.NotEmpty;
import org.phong.horizon.core.enums.Visibility;
import org.phong.horizon.post.enums.PostStatus;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

public record BulkPostUpdateRequest(
    @NotEmpty List<UUID> ids,
    PostStatus status,
    Visibility visibility,
    UUID categoryId
) implements Serializable {
}
