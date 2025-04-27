alter table comments
    add column is_pin boolean default false;

CREATE UNIQUE INDEX unique_pinned_comment_per_post
    ON comments (post_id)
    WHERE is_pin = true;


create table report_comment(
    id uuid primary key,
    comment_id uuid not null,
    user_id uuid not null,
    reason text not null,
    created_at timestamp default now(),
    updated_at timestamp default now(),
    constraint fk_report_comment_user foreign key (user_id) references users(id),
    constraint fk_report_comment_comment foreign key (comment_id) references comments(id)
);

create index idx_report_comment_user_id
    on report_comment (user_id, reason);