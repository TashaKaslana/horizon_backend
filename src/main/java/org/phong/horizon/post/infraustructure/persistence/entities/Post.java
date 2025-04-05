package org.phong.horizon.post.infraustructure.persistence.entities;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.type.SqlTypes;
import org.phong.horizon.infrastructure.enums.Visibility;
import org.phong.horizon.infrastructure.superclass.BaseEntity;
import org.phong.horizon.storage.infrastructure.persistence.entities.Asset;
import org.phong.horizon.user.infrastructure.persistence.entities.User;

import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "posts", indexes = {
        @Index(name = "idx_posts_created_at", columnList = "created_at"),
        @Index(name = "idx_posts_user", columnList = "user_id"),
        @Index(name = "idx_posts_tags", columnList = "tags")
})
@AttributeOverrides({
        @AttributeOverride(name = "createdAt", column = @Column(name = "created_at")),
        @AttributeOverride(name = "updatedAt", column = @Column(name = "updated_at")),
        @AttributeOverride(name = "createdBy", column = @Column(name = "created_by"))
})
public class Post extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "caption", length = Integer.MAX_VALUE)
    private String caption;

    @Column(name = "description", length = Integer.MAX_VALUE)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "video_asset_id", nullable = false)
    private Asset videoAsset;

    @Column(name = "duration", nullable = false)
    private Double duration;

    @Enumerated(EnumType.STRING)
    @Column(name = "visibility", nullable = false, length = 10)
    @ColumnDefault("'PUBLIC'")
    private Visibility visibility;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "tags", columnDefinition = "jsonb")
    private List<String> tags;
}