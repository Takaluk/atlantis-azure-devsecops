create schema if not exists market;

create table if not exists market.stock (
    id bigserial primary key,
    symbol varchar(32) not null unique,
    name varchar(128) not null,
    market varchar(8) not null,
    tag varchar(32) not null
);

create table if not exists market.price_daily (
    id bigserial primary key,
    stock_id bigint not null references market.stock(id) on delete cascade,
    d date not null,
    close numeric(12,2) not null,
    unique(stock_id, d)
);

create table if not exists market.price_event (
    id bigserial primary key,
    stock_id bigint not null references market.stock(id) on delete cascade,
    event_type varchar(16) not null,
    start_date date not null,
    end_date date not null,
    title varchar(200) not null
);
