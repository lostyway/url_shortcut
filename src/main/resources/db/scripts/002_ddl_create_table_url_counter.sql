create table urls
(
    id bigserial primary key,
    original_url   text unique not null,
    short_url   text unique,
    request_count bigint
);