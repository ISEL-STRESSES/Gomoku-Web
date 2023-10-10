BEGIN TRANSACTION;
create table if not exists users
(
    id                  int          generated always as identity primary key,
    username            varchar(64)  not null,
    password_validation varchar(256) not null,

    unique (username)
);

create table if not exists tokens
(
    token_validation varchar(256) not null,
    user_id          int,
    created_at       bigint       not null,
    last_used_at        bigint       not null,

    check (last_used_at >= created_at),
    constraint fk_tokens_user foreign key (user_id) references users(id),
    primary key (token_validation)
);

create table if not exists rules
(
    id     int          generated always as identity primary key,
    board_size   int          not null,
    opening_rule varchar(256) not null default 'free',
    variant      varchar(256) not null default 'standard',

    unique (board_size, opening_rule, variant),
    constraint check_board_size check (board_size = 15 or board_size = 19)
);

create table if not exists user_stats
(
    user_id      int,
    rules_id     int,
    games_played int not null default 0,
    elo          int not null default 0,

    constraint fk_user_stats_user foreign key (user_id) references users(id),
    constraint fk_user_stats_rules foreign key (rules_id) references rules(id),
    primary key (user_id, rules_id)
);

create table if not exists matches
(
    id            int          generated always as identity,
    rules_id      int,
    match_outcome varchar(4)   default null,
    match_state   varchar(256) not null,

    constraint match_outcome_check check (match_outcome is null or match_outcome ~* '^(a|b|draw)$'),
    constraint match_state_check check (match_state ~* '^(waiting_player|ongoing|finished)$'),
    constraint fk_matches_rules foreign key (rules_id) references rules(id),
    primary key (rules_id, id)
);

create table if not exists player
(
    user_id  int,
    match_id int,
    rules_id int,
    color    varchar(5) not null,

    constraint color_check check (color ~* '^(black|white)$'),
    constraint fk_player_user foreign key (user_id) references users(id),
    constraint fk_player_match foreign key (match_id, rules_id) references matches(id, rules_id),
    primary key (user_id, match_id, rules_id)
);

create table if not exists moves
(
    match_id  int,
    rules_id  int,
    player_id int,
    row       int not null,
    col       int not null,

    constraint fk_moves_player foreign key (match_id, rules_id, player_id) references player(match_id, rules_id, user_id),
    primary key (rules_id, match_id, row, col),
    unique (rules_id, match_id, row, col)
);

COMMIT;
