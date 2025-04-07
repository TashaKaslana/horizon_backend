CREATE TABLE follows
(
    follower_id  UUID NOT NULL,
    following_id UUID NOT NULL,
    created_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    primary key (follower_id, following_id),
    check ( follower_id <> following_id )
);

CREATE INDEX idx_follower_id ON follows (follower_id);
CREATE INDEX idx_following_id ON follows (following_id);

ALTER TABLE follows
    ADD CONSTRAINT fk_follower_id
        FOREIGN KEY (follower_id)
            REFERENCES users (id)
            ON DELETE CASCADE;
ALTER TABLE follows
    ADD CONSTRAINT fk_following_id
        FOREIGN KEY (following_id)
            REFERENCES users (id)
            ON DELETE CASCADE;