BEGIN TRANSACTION;

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
    user_id1   int,
    user_id2   int,
    rules_id   int          not null,
    created_at bigint       not null,

    constraint fk_lobby_user foreign key (user_id) references users(id),
    constraint fk_lobby_rules foreign key (rules_id) references rules(id),
    constraint different_users check (user_id1 <> user_id2),
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

-- player table
create table if not exists player
(
    id       int primary key generated always as identity,  -- Added a unique primary key for the player table
    user_id  int not null,
    match_id int,
    rules_id int not null,
    color    varchar(5) not null,

    constraint color_check check (color ~* '^(black|white)$'),
    constraint fk_player_user foreign key (user_id) references users(id),
    unique (match_id, user_id)
);


-- matches table
create table if not exists matches
(
    id            int          primary key generated always as identity,
    rules_id      int          not null,
    player1_id    int,
    player2_id    int,
    match_outcome varchar(4)   default null,
    match_state   varchar(256) not null,

    constraint match_outcome_check check (match_outcome is null or match_outcome ~* '^(a|b|draw)$'),
    constraint match_state_check check (match_state ~* '^(ongoing|finished)$'),
    constraint fk_matches_rules foreign key (rules_id) references rules(id),
    constraint fk_matches_player1 foreign key (player1_id) references player(id),  -- References the new player primary key
    constraint fk_matches_player2 foreign key (player2_id) references player(id)   -- References the new player primary key
);

-- Updating foreign key in player after matches table is created
alter table player
    add constraint fk_player_match foreign key (match_id) references matches(id);

-- moves table
create table if not exists moves
(
    match_id  int not null,
    player_id int not null,
    ordinal   int not null,
    row       int not null,
    col       int not null,

    constraint fk_moves_player foreign key (match_id, player_id) references player(match_id, user_id),
    primary key (match_id, ordinal),
    unique (match_id, row, col)
);

COMMIT;
