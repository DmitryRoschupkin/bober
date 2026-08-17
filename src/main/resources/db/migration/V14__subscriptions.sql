CREATE TABLE subscriptions (
    user_id int references users(id) on delete cascade,
    author_id int references author(id) on delete cascade,
    primary key (user_id, author_id)
)