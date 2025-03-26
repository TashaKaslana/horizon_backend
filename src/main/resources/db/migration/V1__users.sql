CREATE TABLE users
(
    id            UUID         NOT NULL,
    created_at    TIMESTAMP WITHOUT TIME ZONE,
    updated_at    TIMESTAMP WITHOUT TIME ZONE,
    created_by    UUID,
    updated_by    UUID,
    auth0_id      VARCHAR(255) NOT NULL,
    first_name    VARCHAR(100),
    last_name     VARCHAR(100),
    username      VARCHAR(100) NOT NULL,
    email         VARCHAR(255) NOT NULL,
    phone_number  VARCHAR(20),
    date_of_birth date,
    gender        VARCHAR(10),
    bio           TEXT,
    profile_image VARCHAR(255),
    cover_image   VARCHAR(255),
    country       VARCHAR(100),
    city          VARCHAR(100),
    CONSTRAINT pk_users PRIMARY KEY (id)
);

ALTER TABLE users
    ADD CONSTRAINT users_auth0_id_unique UNIQUE (auth0_id);

ALTER TABLE users
    ADD CONSTRAINT users_email_unique UNIQUE (email);

ALTER TABLE users
    ADD CONSTRAINT users_username_unique UNIQUE (username);

ALTER TABLE users
    ADD CONSTRAINT FK_USERS_ON_CREATED_BY FOREIGN KEY (created_by) REFERENCES users (id);

ALTER TABLE users
    ADD CONSTRAINT FK_USERS_ON_UPDATED_BY FOREIGN KEY (updated_by) REFERENCES users (id);