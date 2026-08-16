CREATE TABLE comments (
    id serial primary key,
    user_id int references users(id) on delete cascade,
    book_id int references book(id) on delete cascade,
    comment_text text not null,
    created_at timestamp with time zone default current_timestamp
);

CREATE TABLE marks (
    user_id int references users(id) on delete cascade,
    book_id int references book(id) on delete cascade,
    mark varchar(10) not null check (mark IN ('LIKE', 'DISLIKE')),
    primary key (user_id, book_id)
)