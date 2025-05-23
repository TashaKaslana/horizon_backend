CREATE TYPE admin_notification_type AS ENUM (
    'REPORT',
    'SYSTEM',
    'AUTH',
    'MODERATION',
    'ERROR',
    'QUOTA'
    );

CREATE TYPE notification_severity AS ENUM (
    'INFO',
    'WARNING',
    'CRITICAL'
    );

CREATE TYPE notification_related_type AS ENUM (
    'USER',
    'POST',
    'COMMENT',
    'STORAGE',
    'AUTH',
    'SYSTEM'
    );


CREATE TABLE admin_notifications
(
    id           UUID PRIMARY KEY                 DEFAULT uuid_generate_v4(),

    title        TEXT                    NOT NULL,
    message      TEXT                    NOT NULL,

    type         admin_notification_type NOT NULL,
    severity     notification_severity   NOT NULL,

    source       TEXT,

    related_type notification_related_type,
    related_id   UUID,

    is_read      BOOLEAN                 NOT NULL DEFAULT FALSE,
    created_at   TIMESTAMPTZ             NOT NULL DEFAULT NOW()
);


CREATE INDEX idx_admin_notifications_type ON admin_notifications (type);
CREATE INDEX idx_admin_notifications_severity ON admin_notifications (severity);
CREATE INDEX idx_admin_notifications_created_at ON admin_notifications (created_at);