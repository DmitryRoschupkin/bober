package me.dmitriy.bober.data;


import me.dmitriy.bober.models.Book;

import java.util.List;

public interface BookRepository {
    List<Book> getBooks();
}
