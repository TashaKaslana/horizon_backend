package org.phong.horizon.user.dtos;

import jakarta.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.List;
import java.util.UUID;

public record BulkUserDeleteRequest(
    @NotEmpty List<UUID> userIds
) implements Serializable {
}

