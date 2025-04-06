CREATE TYPE notification_type AS ENUM (
    'LIKE_POST',
    'NEW_FOLLOWER',
    'COMMENT_POST',
    'LIKE_COMMENT',
    'MENTION_COMMENT',
    'REPLY_COMMENT',
    'SYSTEM_MESSAGE'
    );

CREATE TABLE notifications
(
    id                UUID                                   NOT NULL default gen_random_uuid(),
    recipient_user_id UUID                                   NOT NULL,
    sender_user_id    UUID                                   NULL,
    post_id           UUID                                   NULL,
    comment_id        UUID                                   NULL,
    type              notification_type                      NOT NULL,
    content           VARCHAR(255)                           NULL,
    extra_data        JSONB                    DEFAULT '{}'::JSONB,
    is_read           BOOLEAN                  DEFAULT FALSE NOT NULL,
    is_deleted        BOOLEAN                  DEFAULT FALSE NOT NULL,
    created_at        TIMESTAMP WITH TIME ZONE default now() NOT NULL,
    deleted_at        TIMESTAMP WITH TIME ZONE NULL,
    CONSTRAINT pk_notifications PRIMARY KEY (id)
);

CREATE INDEX idx_notifications_recipient_read_created ON notifications (recipient_user_id, is_read, created_at DESC);
CREATE INDEX idx_notifications_recipient_type ON notifications (recipient_user_id, type);
CREATE INDEX idx_notifications_created_deleted ON notifications (created_at, is_deleted);

ALTER TABLE notifications
    ADD CONSTRAINT fk_notifications_recipient FOREIGN KEY (recipient_user_id) REFERENCES users (id) ON DELETE CASCADE;
ALTER TABLE notifications
    ADD CONSTRAINT fk_notifications_sender FOREIGN KEY (sender_user_id) REFERENCES users (id) ON DELETE SET NULL;
ALTER TABLE notifications
    ADD CONSTRAINT fk_notifications_post FOREIGN KEY (post_id) REFERENCES posts (id) ON DELETE SET NULL;
ALTER TABLE notifications
    ADD CONSTRAINT fk_notifications_comment FOREIGN KEY (comment_id) REFERENCES comments (id) ON DELETE SET NULL;



