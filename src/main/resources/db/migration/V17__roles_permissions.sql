CREATE TABLE roles
(
    id          UUID PRIMARY KEY      default uuid_generate_v4(),
    name        VARCHAR(255) NOT NULL,
    slug        VARCHAR(255) NOT NULL UNIQUE,
    description TEXT,
    created_at  TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

INSERT INTO roles (id, name, slug, description)
VALUES (uuid_generate_v4(), 'User', 'user', 'Default user role'),
       (uuid_generate_v4(), 'Moderator', 'moderator', 'Can moderate user content'),
       (uuid_generate_v4(), 'Admin', 'admin', 'System administrator'),
       (uuid_generate_v4(), 'Owner', 'owner', 'App owner with full access');

CREATE TABLE permissions
(
    id          UUID PRIMARY KEY      default uuid_generate_v4(),
    name        VARCHAR(255) NOT NULL,
    slug        VARCHAR(255) NOT NULL UNIQUE,
    description TEXT,
    module      VARCHAR(255) NOT NULL,
    created_at  TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

INSERT INTO permissions (id, name, slug, description, module)
VALUES
    -- Post module
    (uuid_generate_v4(), 'Create Post', 'create_post', 'Allows creating posts', 'post'),
    (uuid_generate_v4(), 'Delete Post', 'delete_post', 'Allows deleting any post', 'post'),
    (uuid_generate_v4(), 'Edit Post', 'edit_post', 'Allows editing posts', 'post'),

    -- Comment module
    (uuid_generate_v4(), 'Delete Comment', 'delete_comment', 'Allows deleting comments', 'comment'),
    (uuid_generate_v4(), 'Moderate Comment', 'moderate_comment', 'Allows moderating comment status', 'comment'),

    -- User module
    (uuid_generate_v4(), 'Ban User', 'ban_user', 'Allows banning a user', 'user'),
    (uuid_generate_v4(), 'Edit User', 'edit_user', 'Allows editing a user profile', 'user'),

    -- Admin panel/system
    (uuid_generate_v4(), 'Access Admin Panel', 'access_admin_panel', 'Access to backend admin interface', 'system'),
    (uuid_generate_v4(), 'Manage Roles', 'manage_roles', 'Manage roles and permissions', 'system');


CREATE TABLE roles_permissions
(
    id            UUID PRIMARY KEY     default uuid_generate_v4(),
    role_id       UUID        NOT NULL,
    permission_id UUID        NOT NULL,
    created_at    TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at    TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    FOREIGN KEY (role_id) REFERENCES roles (id) ON DELETE CASCADE,
    FOREIGN KEY (permission_id) REFERENCES permissions (id) ON DELETE CASCADE,
    UNIQUE (role_id, permission_id)
);

ALTER TABLE users
    ADD COLUMN last_login TIMESTAMP;

ALTER TABLE users
    ADD COLUMN role_id UUID;

UPDATE users
SET role_id = (SELECT id FROM roles WHERE slug = 'user' LIMIT 1)
WHERE role_id IS NULL;

ALTER TABLE users
    ALTER COLUMN role_id SET NOT NULL;

ALTER TABLE users
    ADD CONSTRAINT fk_user_role
        FOREIGN KEY (role_id)
            REFERENCES roles (id)
            ON DELETE SET NULL;

CREATE INDEX idx_users_role_id ON users (role_id);