INSERT INTO "users" (id, username, firstname, surname, avatar, country_code)
VALUES (X'71D2DA637E3E466B8AC560E622A75870', 'alice', null, null, null, 'by'),
       (X'1315EF2C96594B9384A2A1A431B0AAE8', 'bob', null, null, null, 'by');

INSERT INTO friendship (id, user_id, friend_id, state, created_at, updated_at)
VALUES (RANDOM_UUID(), X'71D2DA637E3E466B8AC560E622A75870', X'1315EF2C96594B9384A2A1A431B0AAE8','RECEIVED_PENDING', now(), now());

INSERT INTO friendship (id, user_id, friend_id, state, created_at, updated_at)
VALUES (RANDOM_UUID(), X'1315EF2C96594B9384A2A1A431B0AAE8', X'71D2DA637E3E466B8AC560E622A75870', 'SENT_PENDING', now(), now());