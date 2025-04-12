alter table posts add is_author_deleted boolean default false;
alter table comments add is_author_deleted boolean default false;

