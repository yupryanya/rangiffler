create table users (
    id           binary(16) not null primary key,
    username     varchar(50) not null unique,
    firstname    varchar(100),
    surname      varchar(100),
    avatar       varchar(255),
    country_code varchar(10),
    created_at   timestamp default current_timestamp,
    updated_at   timestamp default current_timestamp on update current_timestamp
);

create table friendship (
    id        binary(16) not null primary key,
    user_id   binary(16) not null,
    friend_id binary(16) not null,
    state     enum('NONE', 'SENT_PENDING', 'RECEIVED_PENDING', 'FRIEND') not null,
    created_at timestamp default current_timestamp,
    updated_at timestamp default current_timestamp on update current_timestamp,

    constraint ux_friendship_user_friend unique (user_id, friend_id),
    constraint fk_friendship_user foreign key (user_id) references users(id) on delete cascade,
    constraint fk_friendship_friend foreign key (friend_id) references users(id) on delete cascade
);

create index idx_friendship_user on friendship(user_id);
create index idx_friendship_friend on friendship(friend_id);
create index idx_friendship_state on friendship(state);
