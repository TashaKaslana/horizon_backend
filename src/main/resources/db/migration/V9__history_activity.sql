CREATE TABLE history_activity
(
    id                   UUID         NOT NULL DEFAULT gen_random_uuid(),
    activity_type        INT          NOT NULL,
    activity_description TEXT         NOT NULL,
    activity_detail      JSONB        NULL, -- update log like when you update a log it would be like {"old": "old value", "new": "new value"}
    target_id            UUID         NULL,
    target_type          VARCHAR(255) NOT NULL,
    user_id              UUID         NOT NULL,
    user_agent           VARCHAR(255) NULL,
    ip_address           VARCHAR(255) NULL,
    created_at           TIMESTAMP    NOT NULL DEFAULT now(),
    PRIMARY KEY (id)
);

CREATE INDEX idx_history_activity_user_type ON history_activity (user_id, activity_type, created_at DESC);
CREATE INDEX idx_history_activity_target_type ON history_activity (target_id, target_type, created_at DESC);
CREATE INDEX idx_history_activity_target_type_user ON history_activity (target_id, target_type, user_id, created_at DESC);
CREATE INDEX idx_history_activity_created_at ON history_activity (created_at DESC);
CREATE INDEX idx_history_activity_type_created ON history_activity (activity_type, created_at DESC);

ALTER TABLE history_activity
    ADD CONSTRAINT fk_history_activity_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE;
ALTER TABLE history_activity
    ADD CONSTRAINT fk_history_activity_type FOREIGN KEY (activity_type) REFERENCES activity_types (id) ON DELETE CASCADE;