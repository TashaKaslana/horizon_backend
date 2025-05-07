alter table users
    add column display_name varchar(50);

CREATE TYPE notification_status AS ENUM (
    'success',
    'warning',
    'error',
    'info'
    );

ALTER TABLE notifications
    ADD COLUMN status notification_status DEFAULT 'info' NOT NULL;

