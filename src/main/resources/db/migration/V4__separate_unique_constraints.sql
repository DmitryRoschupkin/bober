ALTER TABLE users DROP CONSTRAINT IF EXISTS unique_nick_email;

ALTER TABLE users ADD CONSTRAINT users_name_unique UNIQUE (nickname);
ALTER TABLE users ADD CONSTRAINT users_email_unique UNIQUE (email);
