INSERT INTO "users" (id, username, firstname, surname, avatar, country_code)
VALUES  (X'71D2DA637E3E466B8AC560E622A75870', 'alice', null, null, null, 'by'),
        (X'16B60098FE474D3DB1493354C25CD84B', 'john', null, null, null, 'mr'),
        (X'5ED6D5B645F64AF89027BC4FC3398D39', 'anna', null, null, null, 'aq'),
        (X'1315EF2C96594B9384A2A1A431B0AAE8', 'bob', null, null, null, 'cx');

INSERT INTO friendship (id, user_id, friend_id, state, created_at, updated_at)
VALUES (RANDOM_UUID(), X'71D2DA637E3E466B8AC560E622A75870', X'1315EF2C96594B9384A2A1A431B0AAE8','RECEIVED_PENDING', now(), now());

INSERT INTO friendship (id, user_id, friend_id, state, created_at, updated_at)
VALUES (RANDOM_UUID(), X'1315EF2C96594B9384A2A1A431B0AAE8', X'71D2DA637E3E466B8AC560E622A75870', 'SENT_PENDING', now(), now());

INSERT INTO friendship (id, user_id, friend_id, state, created_at, updated_at)
VALUES (RANDOM_UUID(), X'71D2DA637E3E466B8AC560E622A75870', X'16B60098FE474D3DB1493354C25CD84B','SENT_PENDING', now(), now());

INSERT INTO friendship (id, user_id, friend_id, state, created_at, updated_at)
VALUES (RANDOM_UUID(), X'16B60098FE474D3DB1493354C25CD84B', X'71D2DA637E3E466B8AC560E622A75870', 'RECEIVED_PENDING', now(), now());

INSERT INTO friendship (id, user_id, friend_id, state, created_at, updated_at)
VALUES (RANDOM_UUID(), X'71D2DA637E3E466B8AC560E622A75870', X'5ED6D5B645F64AF89027BC4FC3398D39','FRIEND', now(), now());

INSERT INTO friendship (id, user_id, friend_id, state, created_at, updated_at)
VALUES (RANDOM_UUID(), X'5ED6D5B645F64AF89027BC4FC3398D39', X'71D2DA637E3E466B8AC560E622A75870', 'FRIEND', now(), now());