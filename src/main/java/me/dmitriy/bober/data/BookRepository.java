package me.dmitriy.bober.data;

import me.dmitriy.bober.models.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;


public interface BookRepository extends JpaRepository<Book, Integer> {

    Integer id(int id);

    @Query("SELECT DISTINCT b FROM Book b LEFT JOIN FETCH b.authors WHERE b.id = :id")
    Optional<Book> findByIdWithAuthors(@Param("id") int id);
}
