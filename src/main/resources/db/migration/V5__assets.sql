CREATE TABLE assets
(
    id            UUID PRIMARY KEY,                    -- Or UUID
    public_id     VARCHAR(255)  NOT NULL UNIQUE,       -- Cloudinary's Public ID
    secure_url    VARCHAR(1024) NOT NULL,              -- The main URL for display
    resource_type VARCHAR(50)   NOT NULL,              -- 'image', 'video', 'raw'
    format        VARCHAR(50),                         -- e.g., 'jpg', 'png', 'mp4'
    bytes         BIGINT,                              -- File size
    width         INT,                                 -- For images/videos
    height        INT,                                 -- For images/videos
    original_filename VARCHAR(255),                     -- Original file name
    created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- When the record was created in DB
    updated_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- When the record was updated in DB
    created_by    UUID,                                -- User who uploaded the asset
    updated_by    UUID                                 -- User who last updated the asset
);

CREATE INDEX idx_assets_public_id ON assets (public_id);
CREATE INDEX idx_assets_created_by ON assets (created_by);