ALTER TABLE post_categories
    add column slug VARCHAR(255) UNIQUE DEFAULT NULL;

CREATE INDEX idx_post_categories_slug ON post_categories (slug);

CREATE TYPE comment_status AS ENUM (
    'Approved',
    'Pending',
    'Spam',
    'Rejected'
    );

ALTER TABLE comments
    ADD COLUMN status comment_status NOT NULL DEFAULT 'Pending';

CREATE TYPE post_status AS ENUM (
    'Draft',
    'PendingReview',
    'Published',
    'Rejected',
    'Archived'
    );

ALTER TABLE posts
    ADD COLUMN status post_status NOT NULL DEFAULT 'Draft';

CREATE TYPE user_status AS ENUM (
    'Active',
    'Pending',
    'Suspended',
    'Deactivated'
    );


ALTER TABLE users
    ADD COLUMN status user_status NOT NULL DEFAULT 'Pending';