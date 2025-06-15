create table urls
(
    id bigserial primary key,
    url   text unique not null,
    code   text unique,
    request_count bigint
);