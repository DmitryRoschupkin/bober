CREATE TABLE users (
    id serial primary key,
    nickname varchar(30) not null,
    email varchar(50) not null,
    password varchar(255) not null,
    first_name varchar(50) default 'Ludwig',
    last_name varchar (50) default 'White',
    birth_date date,
    bio text,
    role varchar(50) default 'USER',
    created_at timestamp default now()
);
