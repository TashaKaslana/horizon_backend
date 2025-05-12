CREATE TABLE post_views
(
    id         UUID PRIMARY KEY,
    post_id    UUID      NOT NULL REFERENCES posts (id),
    user_id    UUID REFERENCES users (id),
    ip_address TEXT,
    viewed_at  TIMESTAMP NOT NULL DEFAULT now()
);

CREATE INDEX idx_post_views_post ON post_views(post_id);
CREATE INDEX idx_post_views_user_post_time ON post_views(user_id, post_id, viewed_at);

