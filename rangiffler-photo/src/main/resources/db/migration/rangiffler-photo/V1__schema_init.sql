create table if not exists `photo`
(
    id            binary(16) unique not null default (UUID_TO_BIN(UUID(), true)),
    user_id       binary(16)        not null,
    country_code  varchar(50)       not null,
    `description` varchar(255),
    photo         longblob,
    created_date  datetime          not null,
    primary key (id)
    );

create table photo_like (
    photo_id binary(16) not null,
    user_id binary(16) not null,
    primary key (photo_id, user_id),
    foreign key (photo_id) references photo(id)
);


