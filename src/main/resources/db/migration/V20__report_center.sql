CREATE TYPE moderation_status AS ENUM (
    'PENDING',
    'REVIEWED_APPROVED',
    'REVIEWED_REJECTED',
    'ACTIONTAKEN_CONTENTREMOVED',
    'ACTIONTAKEN_USERWARNED',
    'ACTIONTAKEN_USERBANNED',
    'RESOLVED'
    );

CREATE TYPE moderation_item_type AS ENUM (
    'POST',
    'COMMENT',
    'USER'
    );

CREATE TABLE reports
(
    id               UUID PRIMARY KEY              DEFAULT uuid_generate_v4(),

    reason           TEXT                 NOT NULL,
    moderator_notes  TEXT,
    status           moderation_status    NOT NULL DEFAULT 'PENDING',
    item_type        moderation_item_type NOT NULL,

    post_id          UUID,
    comment_id       UUID,
    reported_user_id UUID,
    reporter_id      UUID                 NOT NULL,

    created_at       TIMESTAMPTZ                   DEFAULT NOW(),
    updated_at       TIMESTAMPTZ                   DEFAULT NOW(),

    CONSTRAINT fk_report_reporter FOREIGN KEY (reporter_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_report_post FOREIGN KEY (post_id) REFERENCES posts (id) ON DELETE CASCADE,
    CONSTRAINT fk_report_comment FOREIGN KEY (comment_id) REFERENCES comments (id) ON DELETE CASCADE,
    CONSTRAINT fk_report_user FOREIGN KEY (reported_user_id) REFERENCES users (id) ON DELETE CASCADE,

    CHECK (
        (item_type = 'POST' AND post_id IS NOT NULL AND comment_id IS NULL AND reported_user_id IS NULL) OR
        (item_type = 'COMMENT' AND comment_id IS NOT NULL AND post_id IS NULL AND reported_user_id IS NULL) OR
        (item_type = 'USER' AND reported_user_id IS NOT NULL AND post_id IS NULL AND comment_id IS NULL)
        )
);

drop table post_report;