package me.dmitriy.bober.models;

import lombok.Data;

import java.util.Objects;

@Data
public class Book {

    private int id;
    private String title;
    private String author;
    private String publisher;
    private int year;
    private String description;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Book book = (Book) o;
        return year == book.year && id == book.id &&
                Objects.equals(title, book.title) &&
                Objects.equals(author, book.author) &&
                Objects.equals(publisher, book.publisher) &&
                Objects.equals(description, book.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, author, publisher, year, description);
    }
}
