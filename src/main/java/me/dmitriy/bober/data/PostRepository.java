package me.dmitriy.bober.data;

import me.dmitriy.bober.models.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Integer> {

    @Query("SELECT DISTINCT p FROM Post p" +
            " LEFT JOIN FETCH p.comments" +
            " WHERE p.author.id = :authorId" +
            " ORDER BY p.id DESC")
    List<Post> findAllByAuthorIdWithComments(@Param("authorId") int authorId);
}
