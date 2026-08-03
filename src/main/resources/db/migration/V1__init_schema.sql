create table author (
	id serial primary key,
	first_name varchar(50) not null,
	last_name varchar(50),
	birth_date date,
	bio text
);

create table book (
	id serial primary key,
	title varchar(255) not null,
	genre varchar(50),
	publisher varchar(50),
	year int,
	description text,
	created_at timestamp default now()
);

create table book_authors (
	author_id int references author(id),
	book_id int references book(id),
	primary key(author_id, book_id)
);

