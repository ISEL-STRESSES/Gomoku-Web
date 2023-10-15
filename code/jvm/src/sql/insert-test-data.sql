BEGIN TRANSACTION;

-- Truncating tables first
TRUNCATE TABLE users, tokens, rules, user_stats, matches, player, moves RESTART IDENTITY CASCADE;

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
                EXECUTE 'INSERT INTO tokens(token_validation, user_id, created_at, last_used_at) VALUES (''token' || i || ''', ' || i || ', 1677645600, 1677646600)';
            END LOOP;
    END $$;

-- Inserting data into 'rules' table
INSERT INTO rules(board_size, opening_rule, variant)
VALUES
    (15, 'free', 'standard'),
    (19, 'free', 'standard'),
    (15, 'pro', 'standard');

-- Inserting data into 'user_stats' table
DO $$
    BEGIN
        FOR i IN 1..10 LOOP
                FOR j IN 1..3 LOOP
                        EXECUTE 'INSERT INTO user_stats(user_id, rules_id, games_played, elo) VALUES (' || i || ', ' || j || ', 5, 1500)';
                    END LOOP;
            END LOOP;
    END $$;

-- Step 1: Inserting data into 'matches' table without player references
DO $$
    BEGIN
        FOR i IN 1..10 LOOP
                EXECUTE 'INSERT INTO matches(rules_id, match_outcome, match_state) VALUES (1, NULL, ''ongoing'')';
            END LOOP;
    END $$;

-- Step 2: Inserting data into 'player' table
DO $$
    BEGIN
        FOR i IN 1..10 LOOP
                EXECUTE 'INSERT INTO player(user_id, match_id, rules_id, color) VALUES (' || (2*i - 1) || ', ' || i || ', 1, ''black'')';
                EXECUTE 'INSERT INTO player(user_id, match_id, rules_id, color) VALUES (' || (2*i) || ', ' || i || ', 1, ''white'')';
            END LOOP;
    END $$;

-- Step 3: Update the 'matches' table to set player references
DO $$
    BEGIN
        FOR i IN 1..10 LOOP
                EXECUTE 'UPDATE matches SET player1_id = ' || (2*i - 1) || ', player2_id = ' || (2*i) || ' WHERE id = ' || i;
            END LOOP;
    END $$;

-- Inserting data into 'moves' table
DO $$
    BEGIN
        FOR i IN 1..7 LOOP
                -- For each match, the black player id is 2i-1 and the white player is 2i
                EXECUTE 'INSERT INTO moves(match_id, player_id, ordinal, row, col) VALUES (' || i || ', ' || (2*i - 1) || ', 1, ' || (i) || ', ' || (i) || ')';
                EXECUTE 'INSERT INTO moves(match_id, player_id, ordinal, row, col) VALUES (' || i || ', ' || (2*i) || ', 2, ' || (i + 1) || ', ' || (i + 1) || ')';
            END LOOP;
    END $$;

COMMIT;
