CREATE TABLE post_report
(
    id         uuid PRIMARY KEY,
    reason     TEXT   NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    user_id    uuid NOT NULL,
    post_id    UUID NOT NULL,

    CONSTRAINT fk_report_user FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT fk_report_post FOREIGN KEY (post_id) REFERENCES posts (id)
);

CREATE INDEX idx_post_report_user ON post_report (user_id);
CREATE INDEX idx_post_report_post_reason ON post_report (post_id, reason);
