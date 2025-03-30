CREATE TABLE posts
(
    id            UUID                         NOT NULL,
    created_at    TIMESTAMP WITHOUT TIME ZONE,
    updated_at    TIMESTAMP WITHOUT TIME ZONE,
    created_by    UUID,
    updated_by    UUID,
    user_id       UUID                         NOT NULL,
    caption       TEXT,
    video_url     VARCHAR(255)                 NOT NULL,
    thumbnail_url VARCHAR(255),
    duration      DOUBLE PRECISION             NOT NULL,
    visibility    VARCHAR(10) DEFAULT 'public' NOT NULL,
    tags          JSONB,
    CONSTRAINT pk_posts PRIMARY KEY (id)
);

CREATE INDEX idx_posts_created_at ON posts (created_at);

CREATE INDEX idx_posts_tags ON posts (tags);

ALTER TABLE posts
    ADD CONSTRAINT FK_POSTS_ON_USER FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE;

CREATE INDEX idx_posts_user ON posts (user_id);