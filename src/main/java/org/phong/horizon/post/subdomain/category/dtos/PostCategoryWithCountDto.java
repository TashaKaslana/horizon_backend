package org.phong.horizon.post.subdomain.category.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.phong.horizon.post.subdomain.category.entities.PostCategory;

import java.time.Instant;
import java.util.UUID;

/**
 * DTO that extends PostCategory information with post count details
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostCategoryWithCountDto {
    private UUID id;
    private String name;
    private String description;
    private String slug;
    private Instant createdAt;
    private Instant updatedAt;
    private UUID createdBy;
    private UUID updatedBy;
    private long postCount;

    /**
     * Constructor to create DTO from entity and post count
     */
    public PostCategoryWithCountDto(PostCategory category, long postCount) {
        this.id = category.getId();
        this.name = category.getName();
        this.description = category.getDescription();
        this.slug = category.getSlug();
        this.createdAt = category.getCreatedAt();
        this.updatedAt = category.getUpdatedAt();
        this.createdBy = category.getCreatedBy();
        this.updatedBy = category.getUpdatedBy();
        this.postCount = postCount;
    }
}
