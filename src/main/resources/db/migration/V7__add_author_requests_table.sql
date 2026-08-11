CREATE TABLE author_requests (
                                 id SERIAL PRIMARY KEY,
                                 user_id INT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                                 resume TEXT NOT NULL,
                                 status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
                                 created_at TIMESTAMP NOT NULL DEFAULT now()
);

CREATE UNIQUE INDEX author_requests_one_pending_per_user
    ON author_requests (user_id)
    WHERE status = 'PENDING';