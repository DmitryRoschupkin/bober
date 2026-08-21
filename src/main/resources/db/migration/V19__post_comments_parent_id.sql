ALTER TABLE post_comments ADD COLUMN parent_id int references post_comments(id) on delete cascade;
ALTER TABLE post_comments ADD COLUMN is_deleted BOOLEAN NOT NULL DEFAULT false;
CREATE INDEX idx_post_comments_parent_id ON post_comments(parent_id);
ALTER TABLE post_comments ADD CONSTRAINT chk_post_comments_no_selt_parent CHECK (parent_id IS NULL OR parent_id != id)