create table if not exists 'user' (
    uuid serial primary key,
    username varchar(255) not null,

    unique(username)
);

create table if not exists 'password' (
    user_id int not null primary key,
    encoded_password varchar(255) not null,
    method varchar(255) not null,

    constraint fk_user foreign key(user_id) references 'user'(uuid),
);

create table if not exists 'token' (
    user_id int not null primary key,
    encoded_token varchar(255) not null,
    create_date timestamp not null,
    last_used timestamp not null,
    ttl time not null,

    constraint fk_user foreign key(user_id) references 'user'(uuid),
    constraint positive_ttl check (ttl > 0),
    check (last_used > create_date),
    check (create_date + ttl > last_used)
);

create table if not exists 'stats' (
    user_id int not null primary key,
    elo int not null default 0,
    games_played int not null default 0,

    constraint fk_user foreign key(user_id) references 'user'(uuid)
);

create table if not exists 'match' (
    match_id int not null primary key,
    host_id int not null,

    constraint fk_user foreign key(host_id) references 'user'(uuid)
);

create table if not exists 'ongoing_match' (
    match_id int not null primary key,
    match_host_id int not null,
    guest_id int not null,
    moves array,

    constraint fk_match foreign key(match_id) references 'match'(match_id),
    constraint fk_match foreign key(match_host_id) references 'match'(host_id),
    constraint fk_user foreign key(guest_id) references 'user'(uuid),
    constraint host_different_than_guess check (host_id <> guest_id)
);

create table if not exists 'finished_match' (
    match_id int not null primary key,
    moves array not null,
    winner int not null,

    constraint fk_ongoing_match foreign key(match_id) references 'ongoing_match'(match_id)
);
