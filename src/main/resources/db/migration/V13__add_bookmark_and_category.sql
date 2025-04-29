CREATE TABLE bookmarks
(
    id         UUID                                   NOT NULL,
    user_id    UUID                                   NOT NULL,
    post_id    UUID                                   NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE default now() NOT NULL,
    CONSTRAINT pk_bookmarks PRIMARY KEY (id)
);

CREATE INDEX idx_bookmarks_user_post ON bookmarks (user_id, post_id);

ALTER TABLE bookmarks
    ADD CONSTRAINT fk_bookmarks_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE;
ALTER TABLE bookmarks
    ADD CONSTRAINT fk_bookmarks_post FOREIGN KEY (post_id) REFERENCES posts (id) ON DELETE CASCADE;

ALTER TABLE bookmarks
    ADD CONSTRAINT uq_bookmarks_user_post UNIQUE (user_id, post_id);

CREATE TABLE post_categories
(
    id         UUID                                   NOT NULL,
    name       VARCHAR(255)                           NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE default now() NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE default now() NOT NULL,
    created_by UUID                                   NOT NULL,
    updated_by UUID                                   NOT NULL,
    is_deleted BOOLEAN                  DEFAULT FALSE NOT NULL,
    CONSTRAINT pk_post_categories PRIMARY KEY (id)
);

ALTER TABLE post_categories
    ADD CONSTRAINT uq_post_categories_name UNIQUE (name);

ALTER TABLE posts
    ADD COLUMN category_id UUID;

ALTER TABLE posts
    ADD CONSTRAINT fk_posts_category FOREIGN KEY (category_id) REFERENCES post_categories (id) ON DELETE SET NULL;

CREATE INDEX idx_posts_category_id ON posts (category_id);

INSERT INTO activity_types (code, name, description, category)
VALUES ('post_bookmark', 'Post Bookmark', 'Bookmark a post', 'POST'),
       ('post_unbookmark', 'Post Unbookmark', 'Unbookmark a post', 'POST'),
       ('post_category_create', 'Post Category Created', 'Created a new post category', 'POST'),
       ('post_category_update', 'Post Category Updated', 'Updated a post category', 'POST'),
       ('post_category_delete', 'Post Category Deleted', 'Deleted a post category', 'POST');;
