create schema if not exists content;

create table if not exists content.keyword (
    id bigserial primary key,
    word varchar(64) not null unique,
    description text not null
);

create table if not exists content.article (
    id bigserial primary key,
    title varchar(300) not null,
    meta varchar(100) not null,
    description text not null,
    published_at date not null
);

create table if not exists content.stock_keyword (
    id bigserial primary key,
    stock_id bigint not null,
    keyword_id bigint not null references content.keyword(id) on delete cascade,
    score int not null default 50,
    unique(stock_id, keyword_id)
);

create table if not exists content.stock_article (
    id bigserial primary key,
    stock_id bigint not null,
    article_id bigint not null references content.article(id) on delete cascade,
    unique(stock_id, article_id)
);

create table if not exists content.event_article (
    id bigserial primary key,
    event_id bigint not null,
    stock_id bigint not null,
    article_id bigint not null references content.article(id) on delete cascade,
    unique(event_id, article_id)
);

create table if not exists content.price_event (
    id bigserial primary key,
    stock_id bigint not null,
    title varchar(120) not null,
    type varchar(20) not null,
    start_date date not null,
    end_date date not null
);

create table if not exists content.stock_forecast (
    id bigserial primary key,
    stock_id bigint not null,
    horizon varchar(8) not null,
    direction varchar(12) not null,
    confidence int not null,
    model varchar(60) not null,
    unique(stock_id, horizon)
);

create table if not exists content.stock_forecast_bullet (
    id bigserial primary key,
    forecast_id bigint not null references content.stock_forecast(id) on delete cascade,
    position int not null,
    text varchar(200) not null,
    unique(forecast_id, position)
);

create table if not exists content.stock_forecast_keyword (
    id bigserial primary key,
    stock_id bigint not null,
    keyword_id bigint not null references content.keyword(id) on delete cascade,
    position int not null,
    unique(stock_id, keyword_id)
);
