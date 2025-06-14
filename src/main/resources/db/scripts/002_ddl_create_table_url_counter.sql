create table url_counter
(
    id bigserial primary key,
    url   text unique not null,
    request_count bigint
);