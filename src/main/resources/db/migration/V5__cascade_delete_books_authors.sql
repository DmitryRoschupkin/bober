ALTER TABLE book_authors DROP CONSTRAINT book_authors_author_id_fkey;
ALTER TABLE book_authors DROP CONSTRAINT book_authors_book_id_fkey;

ALTER TABLE book_authors
    ADD CONSTRAINT book_authors_author_id_fkey
        FOREIGN KEY (author_id) REFERENCES author(id) ON DELETE CASCADE;

ALTER TABLE book_authors
    ADD CONSTRAINT book_authors_book_id_fkey
        FOREIGN KEY (book_id) REFERENCES book(id) ON DELETE CASCADE;