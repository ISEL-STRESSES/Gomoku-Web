BEGIN TRANSACTION;

-- drop all tables
drop table if exists matches;
drop table if exists user_stats;
drop table if exists lobby;
drop table if exists rules;
drop table if exists tokens;
drop table if exists users;


-- users table
create table if not exists users
(
    id                  int          generated always as identity primary key,
    username            varchar(64)  not null,
    password_validation varchar(256) not null,

    unique (username)
);

-- tokens table
create table if not exists tokens
(
    token_validation varchar(256) primary key,
    user_id          int          not null,
    created_at       bigint       not null,
    last_used_at     bigint       not null,

    check (last_used_at >= created_at),
    constraint fk_tokens_user foreign key (user_id) references users(id)
);

-- rules table
create table if not exists rules
(
    id           int          generated always as identity primary key,
    board_size   int          not null,
    opening_rule varchar(256) not null default 'FREE',
    variant      varchar(256) not null default 'STANDARD',

    unique (board_size, opening_rule, variant),
    constraint check_board_size check (board_size = 15 or board_size = 19)
);

-- lobby table
create table if not exists lobby
(
    id         int          primary key generated always as identity,
    user_id    int          not null,
    rules_id   int          not null,
    created_at bigint       not null,

    constraint fk_lobby_user foreign key (user_id) references users(id),
    constraint fk_lobby_rules foreign key (rules_id) references rules(id),
    unique (user_id, rules_id)
);

-- user_stats table
create table if not exists user_stats
(
    user_id      int not null,
    rules_id     int not null,
    games_played int not null default 0,
    elo          int not null default 0,

    constraint fk_user_stats_user foreign key (user_id) references users(id),
    constraint fk_user_stats_rules foreign key (rules_id) references rules(id),
    primary key (user_id, rules_id)
);

-- matches table
create table if not exists matches
(
    id            int          primary key generated always as identity,
    rules_id      int          not null,
    player_black  int          not null,
    player_white  int          not null,
    match_outcome varchar(4)   default null,
    match_state   varchar(256) not null,
    moves         INTEGER[]   NOT NULL DEFAULT ARRAY[]::INTEGER[],

    constraint fk_matches_rules foreign key (rules_id) references rules(id),
    constraint fk_matches_player1 foreign key (player_black) references users(id),
    constraint fk_matches_player2 foreign key (player_white) references users(id),
    constraint match_outcome_check check (match_outcome is null or match_outcome ~* '^(black_won|white_won|draw)$'),
    constraint match_state_check check (match_state ~* '^(ongoing|finished)$'),
    constraint different_users check (player_black <> player_white)
);

COMMIT ;
