alter table users
    add column is_login boolean default false not null;

alter table reports
    add column resolved_at timestamp;

alter table post_categories
    add column description text;
