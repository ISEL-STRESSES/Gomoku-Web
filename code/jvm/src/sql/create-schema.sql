
create table if not exists users
(
    id                  int          generated always as identity primary key,
    username            varchar(64)  unique not null,
    elo                 int          not null default 0,
    games_played        int          not null default 0,
    password_validation varchar(256) not null
);

create table if not exists tokens
(
    token_validation varchar(256) not null,
    user_id          int          references users(id),
    created_at       bigint       not null,
    last_used        bigint       not null,

    check (last_used > created_at)
);

create table if not exists lobby
(
    id int primary key
);

create table if not exists enters_lobby
(
    lobby_id int not null,
    user_id  int not null,

    constraint fk_lobby foreign key (lobby_id) references lobby (id),
    constraint fk_user foreign key (user_id) references users (id)
);

create table if not exists rules
(
    lobby_id     int          not null references lobby (id),
    board_size   int          not null,
    opening_rule varchar(256) not null default 'free',
    variant      varchar(256) not null default 'standard',

    unique (board_size, opening_rule, variant),
    primary key (board_size, opening_rule, variant),
    constraint check_board_size check (board_size = 15 or board_size = 19)
);

create table if not exists matches
(
    id                int        primary key,
    player_a_id       int        not null references users(id),
    player_b_id       int        not null references users(id),
    is_player_a_black boolean    not null,
    turn              int        not null,
    moves             text[]     not null,
    winner            varchar(4) default null,
    lobby_id          int        not null references lobby(id),

    constraint winner_check check (winner is null or winner ~* '^(a|b|draw)$')
);
