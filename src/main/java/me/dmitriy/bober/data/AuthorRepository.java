package me.dmitriy.bober.data;

import me.dmitriy.bober.models.Author;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AuthorRepository extends JpaRepository<Author, Integer> {

    @Query("SELECT DISTINCT a FROM Author a LEFT JOIN FETCH a.books WHERE a.id = :id")
    Optional<Author> findByIdWithBooks(@Param("id") int id);

    @Query("SELECT a FROM Author a" +
            " WHERE (:name IS NULL )" +
            " OR LOWER(a.firstName) LIKE LOWER(CONCAT('%', CAST(:name AS string) , '%'))" +
            " OR LOWER(a.lastName) LIKE LOWER(CONCAT('%', CAST(:name AS string), '%'))" +
            " OR LOWER(CONCAT(a.firstName, ' ', a.lastName)) LIKE LOWER(CONCAT('%', CAST(:name AS string), '%'))" +
            " OR LOWER(CONCAT(a.lastName,  ' ', a.firstName)) LIKE LOWER(CONCAT('%', CAST(:name AS string), '%'))")
    List<Author> findByName(@Param("name") String name, Sort sort);

    @Query("SELECT a FROM Author a LEFT JOIN a.books b" +
            " WHERE (:name IS NULL )" +
            " OR LOWER(a.firstName) LIKE LOWER(CONCAT('%', CAST(:name AS string), '%'))" +
            " OR LOWER(a.lastName) LIKE LOWER(CONCAT('%', CAST(:name AS string), '%'))" +
            " OR LOWER(CONCAT(a.firstName, ' ', a.lastName)) LIKE LOWER(CONCAT('%', CAST(:name AS string), '%'))" +
            " OR LOWER(CONCAT(a.lastName,  ' ', a.firstName)) LIKE LOWER(CONCAT('%', CAST(:name AS string), '%'))" +
            " GROUP BY a ORDER BY COUNT(b) DESC")
    List<Author> findByNameOrderByBookCountDesc(@Param("name") String name);

    @Query("SELECT a FROM Author a LEFT JOIN a.subscriptions s" +
            " WHERE (:name IS NULL)" +
            " OR LOWER(a.firstName) LIKE LOWER(CONCAT('%', CAST(:name AS string) , '%'))" +
            " OR LOWER(a.lastName) LIKE LOWER(CONCAT('%', CAST(:name AS string), '%'))" +
            " OR LOWER(CONCAT(a.firstName, ' ', a.lastName)) LIKE LOWER(CONCAT('%', CAST(:name AS string), '%'))" +
            " OR LOWER(CONCAT(a.lastName,  ' ', a.firstName)) LIKE LOWER(CONCAT('%', CAST(:name AS string), '%'))" +
            " GROUP BY a ORDER BY COUNT(s) DESC")
    List<Author> findByNameOrderBySubscribersDesc(@Param("name") String name);

    Optional<Author> findByUserId(int userId);
}
