ALTER TABLE users
ADD CONSTRAINT unique_nick_email UNIQUE (name, email)