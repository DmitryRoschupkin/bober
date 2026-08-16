package me.dmitriy.bober.data;

import me.dmitriy.bober.models.Book;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;


public interface BookRepository extends JpaRepository<Book, Integer> {

    @Query("SELECT DISTINCT b FROM Book b LEFT JOIN FETCH b.authors WHERE b.id = :id")
    Optional<Book> findByIdWithAuthors(@Param("id") int id);

    @Query("SELECT DISTINCT b.genre FROM Book b WHERE b.genre IS NOT NULL")
    List<String> findDistinctGenres();

    @Query("SELECT DISTINCT b FROM Book b " +
            "LEFT JOIN FETCH b.authors a " +
            "WHERE (:title IS NULL OR LOWER(b.title) LIKE LOWER(CONCAT('%', CAST(:title AS string), '%')))" +
            "AND (:genre IS NULL OR b.genre = :genre)" +
            "AND (:year IS NULL OR b.year = :year)" +
            "AND (:authorId IS NULL OR a.id = :authorId)")
    List<Book> findFiltered(
            @Param("title") String title,
            @Param("genre") String genre,
            @Param("year") Integer year,
            @Param("authorId") Integer authorId,
            Sort sort
    );

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE Book b SET b.likesCount = b.likesCount + 1 WHERE b.id = :id")
    void incrementLikes(@Param("id") int id);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE Book b SET b.likesCount = b.likesCount - 1 WHERE b.id = :id AND b.likesCount > 0")
    void decrementLikes(@Param("id") int id);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE Book b SET b.dislikesCount = b.dislikesCount + 1 WHERE b.id = :id")
    void incrementDislikes(@Param("id") int id);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE Book b SET b.dislikesCount = b.dislikesCount - 1 WHERE b.id = :id AND b.dislikesCount > 0")
    void decrementDislikes(@Param("id") int id);
}
