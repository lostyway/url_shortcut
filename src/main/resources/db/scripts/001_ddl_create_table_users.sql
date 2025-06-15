create table users
(
    id       bigserial primary key,
    site     text unique not null,
    username text unique not null,
    password text        not null
);