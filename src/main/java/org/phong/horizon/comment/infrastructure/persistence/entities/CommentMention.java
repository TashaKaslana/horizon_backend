package org.phong.horizon.comment.infrastructure.persistence.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.phong.horizon.user.infrastructure.persistence.entities.User;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Builder
@Entity
@Table(name = "comment_mentions", indexes = {
        @Index(name = "idx_comment_mentions_comment", columnList = "comment_id"),
        @Index(name = "idx_comment_mentions_mentioned_user", columnList = "mentioned_user_id")
}, uniqueConstraints = {
        @UniqueConstraint(name = "uq_comment_mentioned_user", columnNames = {"comment_id", "mentioned_user_id"})
})
@NoArgsConstructor
@AllArgsConstructor
public class CommentMention {
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
    @JoinColumn(name = "mentioned_user_id", nullable = false)
    private User mentionedUser;

    @ColumnDefault("now()")
    @Column(name = "created_at")
    @CreationTimestamp
    private Instant createdAt;
}