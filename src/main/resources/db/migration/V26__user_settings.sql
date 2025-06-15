CREATE TABLE user_settings
(
    id                UUID                     NOT NULL,
    created_at        TIMESTAMP,
    updated_at        TIMESTAMP,
    created_by        UUID,
    updated_by        UUID,
    user_id           UUID                     NOT NULL,
    preferences       JSONB      DEFAULT '{}'::JSONB NOT NULL,
    CONSTRAINT pk_user_settings PRIMARY KEY (id),
    CONSTRAINT uk_user_setting_user UNIQUE (user_id)
);

ALTER TABLE user_settings
    ADD CONSTRAINT fk_user_setting_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE;
ALTER TABLE user_settings
    ADD CONSTRAINT fk_user_setting_created_by FOREIGN KEY (created_by) REFERENCES users(id);
ALTER TABLE user_settings
    ADD CONSTRAINT fk_user_setting_updated_by FOREIGN KEY (updated_by) REFERENCES users(id);
