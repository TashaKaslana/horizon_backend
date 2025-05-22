CREATE TABLE tags
(
    id          UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name        VARCHAR(255) NOT NULL,
    slug        VARCHAR(255) NOT NULL UNIQUE,
    description TEXT,
    created_at  TIMESTAMPTZ      DEFAULT NOW(),
    updated_at  TIMESTAMPTZ      DEFAULT NOW(),
    created_by  UUID,
    updated_by  UUID,


    FOREIGN KEY (created_by) REFERENCES users (id) ON DELETE SET NULL,
    FOREIGN KEY (updated_by) REFERENCES users (id) ON DELETE SET NULL
);

CREATE TABLE post_tags
(
    id         UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    post_id    UUID NOT NULL,
    tag_id     UUID NOT NULL,
    created_at TIMESTAMPTZ      DEFAULT NOW(),
    updated_at TIMESTAMPTZ      DEFAULT NOW(),

    FOREIGN KEY (post_id) REFERENCES posts (id) ON DELETE CASCADE,
    FOREIGN KEY (tag_id) REFERENCES tags (id) ON DELETE CASCADE,
    UNIQUE (post_id, tag_id)
);

CREATE INDEX idx_post_tags_post_id ON post_tags (post_id);
CREATE INDEX idx_post_tags_tag_id ON post_tags (tag_id);
CREATE INDEX idx_tags_slug ON tags (slug);
CREATE INDEX idx_tags_name ON tags (name);


