create schema if not exists usr;

create table if not exists usr.app_user (
    id bigserial primary key,
    email varchar(200) not null unique,
    name varchar(100) not null
);

create table if not exists usr.watchlist (
    id bigserial primary key,
    user_id bigint not null references usr.app_user(id) on delete cascade,
    stock_id bigint not null,
    unique(user_id, stock_id)
);
