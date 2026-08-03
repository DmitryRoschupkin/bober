package me.dmitriy.bober.data;

import me.dmitriy.bober.models.Book;
import org.springframework.data.jpa.repository.JpaRepository;


public interface BookRepository extends JpaRepository<Book, Integer> {

}
