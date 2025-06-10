package org.phong.horizon.post.dtos;

import org.phong.horizon.core.enums.Visibility;
import org.phong.horizon.post.enums.PostStatus;

import java.util.List;
import java.util.UUID;

public record BulkPostUpdateRequest(List<UUID> ids, PostStatus status, Visibility visibility, UUID categoryId) {
}
