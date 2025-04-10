package org.phong.horizon.comment.infrastructure.persistence.entities;

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
import org.phong.horizon.core.enums.InteractionType;
import org.phong.horizon.user.infrastructure.persistence.entities.User;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "comment_interactions", indexes = {
        @Index(name = "idx_comment_interactions_comment_type", columnList = "comment_id, interaction_type"),
        @Index(name = "idx_comment_interactions_user_type", columnList = "user_id, interaction_type")
}, uniqueConstraints = {
        @UniqueConstraint(name = "uq_comment_user_interaction", columnNames = {"comment_id", "user_id", "interaction_type"})
})
public class CommentInteraction {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "comment_id", nullable = false)
    private Comment comment;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "interaction_type", nullable = false, length = 32)
    @Enumerated(EnumType.STRING)
    private InteractionType interactionType;

    @ColumnDefault("now()")
    @Column(name = "created_at")
    @CreationTimestamp
    private Instant createdAt;
}