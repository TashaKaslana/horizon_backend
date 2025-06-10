package org.phong.horizon.post.subdomain.tag.dto;

import java.util.List;
import java.util.UUID;

public record BulkTagDeleteRequest(List<UUID> tagIds) {
}
