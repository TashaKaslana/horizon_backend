package org.phong.horizon.post.subdomain.tag.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.phong.horizon.post.subdomain.tag.entity.Tag;

import java.time.Instant;
import java.util.UUID;

/**
 * DTO that extends Tag information with post count details
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TagWithCountDto {
    private UUID id;
    private String name;
    private String slug;
    private String description;
    private Instant createdAt;
    private Instant updatedAt;
    private UUID createdBy;
    private UUID updatedBy;
    private long postCount;

    /**
     * Constructor to create DTO from entity and post count
     */
    public TagWithCountDto(Tag tag, long postCount) {
        this.id = tag.getId();
        this.name = tag.getName();
        this.slug = tag.getSlug();
        this.description = tag.getDescription();
        this.createdAt = tag.getCreatedAt();
        this.updatedAt = tag.getUpdatedAt();
        this.createdBy = tag.getCreatedBy();
        this.updatedBy = tag.getUpdatedBy();
        this.postCount = postCount;
    }
}
