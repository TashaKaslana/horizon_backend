CREATE TABLE activity_types
(
    id          SERIAL PRIMARY KEY,
    code        VARCHAR(50) UNIQUE NOT NULL, -- for value(machine see this)
    name        VARCHAR(100)       NOT NULL, -- for human-readable
    description TEXT               NULL,
    category    VARCHAR(50)        NULL      -- Optional category (e.g., 'Account', 'Post', 'Interaction', 'Moderation')
);

-- initial data
INSERT INTO activity_types (code, name, description, category)
VALUES ('user_login', 'User Login', 'User logged into their account.', 'Account'),
       ('user_logout', 'User Logout', 'User logged out of their account.', 'Account'),
       ('user_create', 'User Created', 'User account was created.', 'Account'),
       ('user_update', 'User Updated', 'User account was updated.', 'Account'),
       ('user_delete', 'User Deleted', 'User account was deleted.', 'Account'),
       ('password_change', 'Password Change', 'User changed their account password.', 'Account'),
       ('profile_update', 'Profile Update', 'User updated their profile information.', 'Account'),
       ('email_change', 'Email Change', 'User changed their account email.', 'Account'),
       ('account_deletion_request', 'Account Deletion Request', 'User requested account deletion.', 'Account'),
       ('account_recovery_attempt', 'Account Recovery Attempt', 'User attempted to recover their account.', 'Account'),

       ('post_create', 'Created Post', 'User uploaded a new post.', 'Post'),
       ('post_update', 'Updated Post', 'User updated the details of a post.', 'Post'),
       ('post_delete', 'Deleted Post', 'User deleted their post.', 'Post'),

       ('post_view', 'Viewed Post', 'User viewed a post.', 'Interaction'), --could be a mess if too much user
       ('post_like', 'Liked Post', 'User liked a post.', 'Interaction'),
       ('post_unlike', 'Unliked Post', 'User removed their like from a post.', 'Interaction'),
       ('comment_create', 'Created Comment', 'User added a comment to a post.', 'Interaction'),
       ('comment_delete', 'Deleted Comment', 'User deleted their comment.', 'Interaction'),
       ('comment_interaction_create', 'Created Comment Interaction', 'User interacted with a comment (e.g., liked).',
        'Interaction'),
       ('comment_interaction_delete', 'Deleted Comment Interaction', 'User removed their interaction with a comment.',
        'Interaction'),
       ('user_follow', 'Followed User', 'User started following another user.', 'Interaction'),
       ('user_unfollow', 'Unfollowed User', 'User stopped following another user.', 'Interaction'),

       ('post_report', 'Reported Post', 'User reported a post.', 'Moderation'),
       ('user_report', 'Reported User', 'User reported another user.', 'Moderation'),
       ('content_moderation_action', 'Content Moderation', 'Admin/System took action on content.',
        'Moderation'),

       ('asset_create', 'Created Asset', 'User created a new asset.', 'Asset'),
       ('asset_update', 'Updated Asset', 'User updated an existing asset.', 'Asset'),
       ('asset_delete', 'Deleted Asset', 'User deleted an asset.', 'Asset'),
       ('asset_signature', 'Signed Asset', 'User signed an asset.', 'Asset'),

       ('suspicious_activity_alert', 'Suspicious Activity Alert', 'System detected potentially suspicious activity.',
        'Security');
