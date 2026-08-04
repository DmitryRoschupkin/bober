package me.dmitriy.bober.data;

import me.dmitriy.bober.models.Author;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AuthorRepository extends JpaRepository<Author, Long> {

    @Query("SELECT DISTINCT a FROM Author a RIGHT JOIN FETCH a.books WHERE a.id = :id")
    Optional<Author> findByIdWithBooks(@Param("id") int id);

    @Query("SELECT a FROM Author a" +
            " WHERE LOWER(a.firstName) ILIKE LOWER(:name)" +
            " OR LOWER(a.lastName) ILIKE LOWER(:name)" +
            "OR LOWER(CONCAT(a.firstName, ' ', a.lastName)) ILIKE LOWER(:name)" +
            "OR LOWER(CONCAT(a.lastName,  ' ', a.firstName)) ILIKE LOWER(:name)")
    List<Author> findByName(@Param("name") String name);
}
