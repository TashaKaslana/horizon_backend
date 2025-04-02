CREATE TABLE comment_mentions
(
    id                UUID,
    comment_id        UUID NOT NULL,
    mentioned_user_id UUID NOT NULL,
    created_at        TIMESTAMP WITHOUT TIME ZONE DEFAULT now(),
    CONSTRAINT pk_comment_mentions PRIMARY KEY (id)
);

CREATE INDEX idx_comment_mentions_comment ON comment_mentions (comment_id);
CREATE INDEX idx_comment_mentions_mentioned_user ON comment_mentions (mentioned_user_id);

ALTER TABLE comment_mentions
    ADD CONSTRAINT uq_comment_mentioned_user UNIQUE (comment_id, mentioned_user_id);

ALTER TABLE comment_mentions
    ADD CONSTRAINT FK_COMMENT_MENTIONS_ON_COMMENT FOREIGN KEY (comment_id) REFERENCES comments (id) ON DELETE CASCADE;
ALTER TABLE comment_mentions
    ADD CONSTRAINT FK_COMMENT_MENTIONS_ON_MENTIONED_USER FOREIGN KEY (mentioned_user_id) REFERENCES users (id) ON DELETE CASCADE;

-- interaction table
CREATE TABLE comment_interactions
(
    id                UUID,
    comment_id        UUID NOT NULL,
    user_id           UUID NOT NULL,
    interaction_type  VARCHAR(32) NOT NULL,
    created_at        TIMESTAMP WITHOUT TIME ZONE DEFAULT now(),
    CONSTRAINT pk_comment_interactions PRIMARY KEY (id)
);

CREATE INDEX idx_comment_interactions_comment_type ON comment_interactions(comment_id, interaction_type);
CREATE INDEX idx_comment_interactions_user_type ON comment_interactions(user_id, interaction_type);

ALTER TABLE comment_interactions
    ADD CONSTRAINT uq_comment_user_interaction UNIQUE (comment_id, user_id, interaction_type);

ALTER TABLE comment_interactions
    ADD CONSTRAINT FK_COMMENT_INTERACTIONS_ON_COMMENT FOREIGN KEY (comment_id) REFERENCES comments (id) ON DELETE CASCADE;
ALTER TABLE comment_interactions
    ADD CONSTRAINT FK_COMMENT_INTERACTIONS_ON_USER FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE;
