create schema dbo;

create table if not exists dbo.Users
(
    id int generated always as identity primary key,
    username varchar(64) unique not null,
    elo int not null default 0,
    games_played int not null default 0,
    password_validation varchar(256) not null
);

create table if not exists dbo.Tokens
(
    token_validation varchar(256) not null,
    user_id int references dbo.Users(id),
    created_at bigint not null,
    last_used bigint not null,

    check (last_used > create_date)
);

create table if not exists "matches"
(
    match_id int primary key,
    host_id  int not null,

    unique (match_id, host_id),
    constraint fk_user foreign key (host_id) references "user" (uuid)
);

create table if not exists "ongoing_matches"
(
    match_id      int primary key,
    match_host_id int not null,
    guest_id      int not null,
    moves         text[],

    constraint fk_match foreign key (match_id, match_host_id) references "match" (match_id, host_id),
    constraint fk_user foreign key (guest_id) references "user" (uuid),
    constraint host_different_than_guess check (match_host_id <> guest_id)
);

create table if not exists "finished_matches"
(
    match_id int primary key,
    moves    text[] not null,
    winner   int    not null,

    constraint fk_ongoing_match foreign key (match_id) references "ongoing_match" (match_id)
);