create table users
(
    id       bigserial primary key,
    login    text unique not null,
    password text not null
);