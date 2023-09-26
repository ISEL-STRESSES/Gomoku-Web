//database model
CREATE TABLE IF NOT EXISTS 'user' (
    id int NOT NULL PRIMARY KEY,
    username varchar(255) NOT NULL,

    unique(username)
);


CREATE TABLE IF NOT EXISTS 'password' (
    user_id int NOT NULL PRIMARY KEY REFERENCES 'user'(uuid),
    encoded_password varchar(255) NOT NULL,
    method varchar(255) NOT NULL
);


CREATE TABLE IF NOT EXISTS 'token' (
    user_id int NOT NULL PRIMARY KEY REFERENCES 'user'(uuid),
    encoded_token varchar(255) NOT NULL,
    create_date timestamp NOT NULL,
    last_used timestamp NOT NULL,
    ttl time NOT NULL,

    CONSTRAINT positive_ttl CHECK (ttl > 0),
    CHECK (last_used > create_date),
    CHECK (create_date + ttl > last_used)
);


CREATE TABLE IF NOT EXISTS 'stats' (
    user_id int NOT NULL PRIMARY KEY REFERENCES 'user'(uuid)
    elo int NOT NULL DEFAULT 0,
    games_played int NOT NULL DEFAULT 0
);


CREATE TABLE IF NOT EXISTS 'match' (
    match_id int NOT NULL PRIMARY KEY,
    host_id int NOT NULL REFERENCES 'user'(uuid),
);


CREATE TABLE IF NOT EXISTS 'ongoing_match' (
    match_id int NOT NULL PRIMARY KEY REFERENCES 'match'(match_id),
    match_host_id int NOT NULL REFERENCES 'match'(host_id),
    guest_id int REFERENCES 'user'(uuid),
    moves ARRAY,

    CONSTRAINT host_different_than_guess CHECK(host_id <> guest_id)
);


CREATE TABLE IF NOT EXISTS 'finished_match' (
    match_id int NOT NULL PRIMARY KEY REFERENCES 'match'(match_id),
    moves ARRAY NOT NULL,
    winner int NOT NULL
);
