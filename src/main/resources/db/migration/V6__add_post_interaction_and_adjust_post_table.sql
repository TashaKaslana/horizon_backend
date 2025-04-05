CREATE TABLE post_interactions
(
    id          UUID                                       NOT NULL,
    user_id     UUID                                       NOT NULL,
    post_id     UUID                                       NOT NULL,
    interaction VARCHAR(32)                 DEFAULT 'LIKE' NOT NULL,
    created_at  TIMESTAMP WITHOUT TIME ZONE default now(),
    CONSTRAINT pk_post_interactions PRIMARY KEY (id)
);

CREATE INDEX idx_post_interactions_post ON post_interactions (post_id, interaction);
CREATE INDEX idx_post_interactions_user ON post_interactions (user_id, interaction);

ALTER TABLE post_interactions
    ADD CONSTRAINT uq_post_user_interaction UNIQUE (post_id, user_id, interaction);

ALTER TABLE post_interactions
    ADD CONSTRAINT FK_POST_INTERACTIONS_ON_POST FOREIGN KEY (post_id) REFERENCES posts (id) ON DELETE CASCADE;
ALTER TABLE post_interactions
    ADD CONSTRAINT FK_POST_INTERACTIONS_ON_USER FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE;


ALTER TABLE posts
    DROP COLUMN video_url;
ALTER TABLE posts
    DROP COLUMN thumbnail_url;

ALTER TABLE posts
    ADD COLUMN video_asset_id UUID default null;

ALTER TABLE posts
    ADD CONSTRAINT fk_posts_video_asset FOREIGN KEY (video_asset_id) REFERENCES assets (id);