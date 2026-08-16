ALTER TABLE comments ADD COLUMN parent_id int references comments(id) on delete cascade;
ALTER TABLE comments ADD COLUMN is_deleted BOOLEAN NOT NULL DEFAULT false;
CREATE INDEX idx_comments_parent_id ON comments(parent_id);
ALTER TABLE comments ADD CONSTRAINT chk_comments_no_self_parent CHECK (parent_id IS NULL OR parent_id != id);