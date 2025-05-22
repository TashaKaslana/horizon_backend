package org.phong.horizon.post.infrastructure.persistence.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.phong.horizon.core.superclass.BaseEntity;

@Getter
@Setter
@Entity
@Table(name = "post_categories", indexes = {
        @Index(name = "idx_post_categories_slug", columnList = "slug", unique = true)
})
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostCategory extends BaseEntity {
    @Size(max = 255)
    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @Size(max = 255)
    @NotNull
    @Column(name = "slug", nullable = false, unique = true)
    private String slug;
}