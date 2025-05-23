ALTER TABLE post_categories
    add column slug VARCHAR(255) UNIQUE DEFAULT NULL;

CREATE INDEX idx_post_categories_slug ON post_categories (slug);

CREATE TYPE comment_status AS ENUM (
    'APPROVED',
    'PENDING',
    'SPAM',
    'REJECTED'
    );

CREATE TYPE post_status AS ENUM (
    'DRAFT',
    'PENDING_REVIEW',
    'PUBLISHED',
    'REJECTED',
    'ARCHIVED'
    );

CREATE TYPE user_status AS ENUM (
    'ACTIVE',
    'PENDING',
    'SUSPENDED',
    'DEACTIVATED'
    );


ALTER TABLE users
    ADD COLUMN status user_status NOT NULL DEFAULT 'PENDING';


ALTER TABLE posts
    ADD COLUMN status post_status NOT NULL DEFAULT 'DRAFT';


ALTER TABLE comments
    ADD COLUMN status comment_status NOT NULL DEFAULT 'PENDING';
