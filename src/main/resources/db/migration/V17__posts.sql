CREATE TABLE posts (
    id serial primary key,
    author_id int references author(id) on delete cascade,
    title varchar(50),
    post_text text not null,
    likes_count int default 0 not null,
    dislikes_count int default 0 not null,
    created_at timestamp with time zone default current_timestamp
);

CREATE TABLE post_comments (
    id serial primary key,
    user_id int references users(id) on delete cascade,
    post_id int references posts(id) on delete cascade,
    comment_text text not null,
    created_at timestamp with time zone default current_timestamp
);

CREATE TABLE post_marks (
    user_id int references users(id) on delete cascade,
    post_id int references posts(id) on delete cascade,
    is_like boolean not null,
    primary key (user_id, post_id)
);

CREATE INDEX idx_posts_author_id ON posts(author_id);
CREATE INDEX idx_posts_created_at ON posts(created_at DESC);
CREATE INDEX idx_post_comments_post_id ON post_comments(post_id);
CREATE INDEX idx_post_comments_user_id ON post_comments(user_id);