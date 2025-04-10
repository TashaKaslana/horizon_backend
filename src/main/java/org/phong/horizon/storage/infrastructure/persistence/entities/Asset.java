package org.phong.horizon.storage.infrastructure.persistence.entities;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.phong.horizon.core.superclass.BaseEntity;

@Getter
@Setter
@Builder
@Entity
@Table(name = "assets", indexes = {
        @Index(name = "idx_assets_public_id", columnList = "public_id"),
        @Index(name = "idx_assets_created_by", columnList = "created_by")
}, uniqueConstraints = {
        @UniqueConstraint(name = "assets_public_id_key", columnNames = {"public_id"})
})
@AttributeOverrides({
        @AttributeOverride(name = "createdAt", column = @Column(name = "created_at")),
        @AttributeOverride(name = "updatedAt", column = @Column(name = "updated_at"))
})
@NoArgsConstructor
@AllArgsConstructor
public class Asset extends BaseEntity {
    @Column(name = "public_id", nullable = false)
    private String publicId;

    @Column(name = "secure_url", nullable = false, length = 1024)
    private String secureUrl;

    @Column(name = "resource_type", nullable = false, length = 50)
    private String resourceType;

    @Column(name = "format", length = 50)
    private String format;

    @Column(name = "bytes")
    private Long bytes;

    @Column(name = "width")
    private Integer width;

    @Column(name = "height")
    private Integer height;

    @Column(name = "original_filename")
    private String originalFilename;
}