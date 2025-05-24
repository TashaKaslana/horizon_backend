drop table if exists report_comment;
DROP TABLE IF EXISTS post_report;

ALTER TABLE roles
    ADD column created_by UUID;
ALTER TABLE roles
    ADD column updated_by UUID;

ALTER TABLE permissions
    ADD column created_by UUID;
ALTER TABLE permissions
    ADD column updated_by UUID;

INSERT INTO roles_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r,
     permissions p
WHERE r.slug = 'user'
  AND p.slug IN (
                 'create_post',
                 'edit_post'
    );

INSERT INTO roles_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r,
     permissions p
WHERE r.slug = 'moderator'
  AND p.slug IN (
                 'create_post',
                 'edit_post',
                 'delete_comment',
                 'moderate_comment'
    );


INSERT INTO roles_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r,
     permissions p
WHERE r.slug = 'admin'
  AND p.slug IN (
                 'create_post',
                 'edit_post',
                 'delete_post',
                 'delete_comment',
                 'moderate_comment',
                 'ban_user',
                 'edit_user',
                 'access_admin_panel',
                 'manage_roles'
    );


INSERT INTO roles_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r
         JOIN permissions p ON TRUE
WHERE r.slug = 'owner';
