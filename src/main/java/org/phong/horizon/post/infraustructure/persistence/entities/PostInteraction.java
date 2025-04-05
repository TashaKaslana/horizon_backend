package org.phong.horizon.post.infraustructure.persistence.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.phong.horizon.infrastructure.enums.InteractionType;
import org.phong.horizon.user.infrastructure.persistence.entities.User;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "post_interactions", indexes = {
        @Index(name = "idx_post_interactions_user", columnList = "user_id, interaction"),
        @Index(name = "idx_post_interactions_post", columnList = "post_id, interaction")
}, uniqueConstraints = {
        @UniqueConstraint(name = "uq_post_user_interaction", columnNames = {"post_id", "user_id", "interaction"})
})
public class PostInteraction {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @ColumnDefault("'LIKE'")
    @Enumerated(EnumType.STRING)
    @Column(name = "interaction", nullable = false, length = 32)
    private InteractionType interaction;

    @ColumnDefault("now()")
    @Column(name = "created_at")
    @CreationTimestamp
    private Instant createdAt;
}