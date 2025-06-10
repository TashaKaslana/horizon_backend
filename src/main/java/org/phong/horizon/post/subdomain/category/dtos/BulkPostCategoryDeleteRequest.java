package org.phong.horizon.post.subdomain.category.dtos;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import java.util.UUID;

public record BulkPostCategoryDeleteRequest(
    @NotEmpty(message = "Post category IDs cannot be empty")
    List<UUID> postCategoryIds
) {
}

