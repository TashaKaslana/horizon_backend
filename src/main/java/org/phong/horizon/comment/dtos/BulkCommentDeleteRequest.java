package org.phong.horizon.comment.dtos;

import jakarta.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.List;
import java.util.UUID;

public record BulkCommentDeleteRequest(
    @NotEmpty List<UUID> commentIds
) implements Serializable {
}

