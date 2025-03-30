CREATE TABLE comments
(
    id              UUID         NOT NULL,
    created_at      TIMESTAMP WITHOUT TIME ZONE DEFAULT now(),
    updated_at      TIMESTAMP WITHOUT TIME ZONE DEFAULT now(),
    created_by      UUID,
    updated_by      UUID,
    post_id         UUID         NOT NULL,
    user_id         UUID         NOT NULL,
    content         TEXT         NOT NULL,
    parent_comment_id UUID NULL,
    CONSTRAINT pk_comments PRIMARY KEY (id)
);

ALTER TABLE comments
    ADD CONSTRAINT FK_COMMENTS_ON_CREATED_BY FOREIGN KEY (created_by) REFERENCES users (id);

ALTER TABLE comments
    ADD CONSTRAINT FK_COMMENTS_ON_UPDATED_BY FOREIGN KEY (updated_by) REFERENCES users (id);

ALTER TABLE comments
    ADD CONSTRAINT FK_COMMENTS_ON_POST FOREIGN KEY (post_id) REFERENCES posts (id) ON DELETE CASCADE;

ALTER TABLE comments
    ADD CONSTRAINT FK_COMMENTS_ON_USER FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE;

ALTER TABLE comments
    ADD CONSTRAINT FK_COMMENTS_ON_PARENT FOREIGN KEY (parent_comment_id) REFERENCES comments (id) ON DELETE CASCADE;

CREATE INDEX idx_comments_post ON comments(post_id);
CREATE INDEX idx_comments_user ON comments(user_id);
CREATE INDEX idx_comments_created_at ON comments(created_at);
