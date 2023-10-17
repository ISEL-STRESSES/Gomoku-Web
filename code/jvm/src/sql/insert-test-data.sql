BEGIN TRANSACTION;

-- Truncating tables first
TRUNCATE TABLE users, tokens, rules, user_stats, matches RESTART IDENTITY CASCADE;

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
    (15, 'FREE', 'STANDARD'),
    (19, 'FREE', 'STANDARD'),
    (15, 'PRO', 'STANDARD');

-- Inserting data into 'user_stats' table
DO $$
    BEGIN
        FOR i IN 1..10 LOOP
                FOR j IN 1..3 LOOP
                        EXECUTE 'INSERT INTO user_stats(user_id, rules_id, games_played, elo) VALUES (' || i || ', ' || j || ', 5, 1500)';
                    END LOOP;
            END LOOP;
    END $$;

-- Step 1: Inserting data into 'matches' without moves
DO $$
    BEGIN
        FOR i IN 1..10 LOOP
                EXECUTE 'INSERT INTO matches(rules_id, player_black, player_white, match_outcome, match_state) VALUES (1,'||i||','||2*i||', NULL, ''ONGOING'')';
            END LOOP;
    END $$;

-- Step 2: Inserting data into 'matches' with moves

DO $$
    BEGIN
        FOR i IN 1..20 LOOP
                EXECUTE 'UPDATE matches set moves = ''{' || i || ', ' || (i + 1) || '}'' where id =  '|| i||' ';
            END LOOP;
    END $$;

COMMIT;
