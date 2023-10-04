create table if not exists "users"
(
    uuid             serial primary key,
    username         varchar(255) not null,
    encoded_password varchar(255) not null,
    elo              int          not null default 0,
    games_played     int          not null default 0,

    unique (username)
);

create table if not exists "tokens"
(
    user_id       int primary key,
    encoded_token varchar(255) not null,
    create_date   timestamp    not null,
    last_used     timestamp    not null,
    ttl           interval     not null,

    constraint fk_user foreign key (user_id) references "user" (uuid),
    constraint positive_ttl check (ttl > interval '0 0:00:00.000'),
    check (last_used > create_date),
    check (create_date + ttl > last_used)
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