CREATE TABLE log_entries
(
    id         UUID PRIMARY KEY      DEFAULT uuid_generate_v4(),
    timestamp  TIMESTAMPTZ  NOT NULL,
    severity   VARCHAR(10)  NOT NULL CHECK (severity IN ('INFO', 'WARNING', 'ERROR', 'CRITICAL')),
    message    TEXT         NOT NULL,
    source     VARCHAR(255) NOT NULL,
    user_id    UUID,
    context    JSONB,
    created_at TIMESTAMPTZ  NOT NULL DEFAULT NOW(),

    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE SET NULL
);
