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
        FOR i IN 1..10 LOOP
                EXECUTE 'UPDATE matches set moves = ''{' || i || ', ' || (i + 1) || '}'' where id =  '|| i||' ';
            END LOOP;
    END $$;

-- Step 3: Make 1 match be 1 move away from being finished
DO $$
    DECLARE match_id int;
            user_id1 int;
            user_id2 int;
            rule_id int;
            moves_game int[];
    BEGIN
        insert into users (username, password_validation) values ('TestUserDB1', 'ByQYP78&j7Aug2') returning id into user_id1;
        insert into users (username, password_validation) values ('TestUserDB2', 'ByQYP78&j7Aug2') returning id into user_id2;
        select id into rule_id from rules where board_size = 15 and opening_rule = 'FREE' and variant = 'STANDARD';
        if(rule_id IS NULL) then
            insert into rules(board_size, opening_rule, variant) values (15, 'FREE', 'STANDARD') returning id into rule_id;
        end if;
        insert into matches(rules_id, player_black, player_white, match_outcome, match_state) values (rule_id, user_id1, user_id2, NULL, 'ONGOING') returning id into match_id;
        UPDATE matches set moves = '{0, 10, 1, 20, 2, 30, 3, 31}' where id = match_id returning moves into moves_game;
    END $$;

COMMIT;
