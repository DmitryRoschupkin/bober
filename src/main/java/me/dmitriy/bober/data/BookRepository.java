package me.dmitriy.bober.data;

import me.dmitriy.bober.models.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;


public interface BookRepository extends JpaRepository<Book, Integer> {

    Integer id(int id);

    @Query("SELECT DISTINCT b FROM Book b LEFT JOIN FETCH b.authors WHERE b.id = :id")
    Optional<Book> findByIdWithAuthors(@Param("id") int id);

    @Query("SELECT b FROM Book b LEFT JOIN FETCH b.authors WHERE LOWER(b.title) LIKE LOWER(:title) ORDER BY b.title")
    List<Book> findByTitle(@Param("title") String title);

    @Query("SELECT DISTINCT b.genre FROM Book b WHERE b.genre IS NOT NULL")
    List<String> findDistinctGenres();

    @Query("SELECT DISTINCT b FROM Book b " +
            "LEFT JOIN FETCH b.authors a " +
            "WHERE (:genre IS NULL OR b.genre = :genre)" +
            "AND (:year IS NULL OR b.year = :year)" +
            "AND (:authorId IS NULL OR a.id = :authorId)")
    List<Book> findFiltered(
            @Param("genre") String genre,
            @Param("year") Integer year,
            @Param("authorId") Integer authorId
    );
}
