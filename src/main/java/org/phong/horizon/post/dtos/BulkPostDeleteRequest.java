package org.phong.horizon.post.dtos;

import jakarta.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.List;
import java.util.UUID;

public record BulkPostDeleteRequest(
    @NotEmpty List<UUID> postIds
) implements Serializable {
}

