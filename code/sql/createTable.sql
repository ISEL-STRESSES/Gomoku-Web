CREATE TABLE IF NOT EXISTS `user` (
  `id` int NOT NULL,
  `username` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
);

CREATE TABLE IF NOT EXISTS 'stats' (
    'points' int NOT NULL DEFAULT 0,
);

CREATE TABLE IF NOT EXISTS 'game' (
    'game_id' int NOT NULL,
    'moves' ARRAY NOT NULL,
);

CREATE TABLE IF NOT EXISTS 'token' (
    'encoded_token' varchar(255) NOT NULL,
);

CREATE TABLE IF NOT EXISTS 'password' (
    'password' varchar(255) NOT NULL,
);