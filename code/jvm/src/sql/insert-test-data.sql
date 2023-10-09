BEGIN TRANSACTION;
-- Inserting data into 'users' table
INSERT INTO users(username, password_validation) VALUES ('user1', 'password1');
INSERT INTO users(username, password_validation) VALUES ('user2', 'password2');

-- Inserting data into 'tokens' table
-- Using epoch timestamp for created_at and last_used for simplicity. In practice, you'll use the current timestamp or a different mechanism.
INSERT INTO tokens(token_validation, user_id, created_at, last_used) VALUES ('token1', 1, 1677645600, 1677646600);
INSERT INTO tokens(token_validation, user_id, created_at, last_used) VALUES ('token2', 2, 1677645600, 1677646600);

-- Inserting data into 'rules' table
INSERT INTO rules(board_size, opening_rule, variant) VALUES (15, 'free', 'standard');
INSERT INTO rules(board_size, opening_rule, variant) VALUES (19, 'free', 'standard');

-- Inserting data into 'user_stats' table
INSERT INTO user_stats(user_id, rules_id, games_played, elo) VALUES (1, 1, 5, 1500);
INSERT INTO user_stats(user_id, rules_id, games_played, elo) VALUES (2, 1, 4, 1450);

-- Inserting data into 'matches' table
INSERT INTO matches(rules_id, match_outcome, match_state) VALUES (1, NULL, 'ongoing');

--TODO CREATE A TEST THAT WILL MAKE SURE THAT A GAME IN WAITING_FOR_PLAYER CAN'T HAVE TO PLAYERS, AND OR ANY MOVES

-- Inserting data into 'player' table
INSERT INTO player(user_id, match_id, rules_id, color) VALUES (1, 1, 1, 'black');
INSERT INTO player(user_id, match_id, rules_id, color) VALUES (2, 1, 1, 'white');

-- Inserting data into 'moves' table
-- Note: This is just a sample move. In practice, the moves will depend on the game state.
INSERT INTO moves(match_id, rules_id, player_id, row, col) VALUES (1, 1, 1, 5, 5);
INSERT INTO moves(match_id, rules_id, player_id, row, col) VALUES (1, 1, 2, 6, 6);

COMMIT;

TRUNCATE TABLE users, tokens, rules, user_stats, matches, player, moves RESTART IDENTITY CASCADE;

BEGIN TRANSACTION;
-- Inserting data into 'users' table
DO $$
    BEGIN
        FOR i IN 1..20 LOOP
                EXECUTE 'INSERT INTO users(username, password_validation) VALUES (''user' || i || ''', ''password' || i || ''')';
            END LOOP;
    END $$;

-- Inserting data into 'tokens' table
DO $$
    BEGIN
        FOR i IN 1..10 LOOP
                EXECUTE 'INSERT INTO tokens(token_validation, user_id, created_at, last_used) VALUES (''token' || i || ''', ' || i || ', 1677645600, 1677646600)';
            END LOOP;
    END $$;

-- Inserting data into 'rules' table
insert into rules(board_size, opening_rule, variant) VALUES (15, 'free', 'standard');
insert into rules(board_size, opening_rule, variant) VALUES (19, 'free', 'standard');
insert into rules(board_size, opening_rule, variant) VALUES (15, 'pro', 'standard');


-- Inserting data into 'user_stats' table
DO $$
    BEGIN
        FOR i IN 1..10 LOOP
                FOR j IN 1..2 LOOP
                        EXECUTE 'INSERT INTO user_stats(user_id, rules_id, games_played, elo) VALUES (' || i || ', ' || j || ', 5, 1500)';
                    END LOOP;
            END LOOP;
    END $$;

-- Inserting data into 'matches' table
DO $$
    BEGIN
        FOR i IN 1..10 LOOP
                EXECUTE 'INSERT INTO matches(rules_id, match_outcome, match_state) VALUES (1, NULL, ''waiting_player'')';
            END LOOP;
    END $$;

-- Inserting data into 'player' table
DO $$
    BEGIN
        FOR i IN 1..10 LOOP
                EXECUTE 'INSERT INTO player(user_id, match_id, rules_id, color) VALUES (' || i || ', ' || i || ', 1, ''black'')';
                EXECUTE 'INSERT INTO player(user_id, match_id, rules_id, color) VALUES (' || (i + 1) || ', ' || i || ', 1, ''white'')';
            END LOOP;
    END $$;

-- Inserting data into 'moves' table
DO $$
    BEGIN
        FOR i IN 1..7 LOOP
            FOR j IN i..i+1 LOOP
                EXECUTE 'INSERT INTO moves(match_id, rules_id, player_id, row, col) VALUES (' || i || ', 1, ' || j || ', ' || (i+j) || ', ' || (i+j) || ')';
            END LOOP;
        END LOOP;
END $$;
COMMIT ;