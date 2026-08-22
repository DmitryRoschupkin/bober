package me.dmitriy.bober.data;

import me.dmitriy.bober.models.Post;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Integer> {

    List<Post> findByAuthorId(Integer authorId, Sort sort);

    @Query("SELECT p FROM Post p" +
            " WHERE p.author.id = :authorId" +
            " ORDER BY (COALESCE(p.likesCount, 0) + COALESCE(p.dislikesCount, 0) + SIZE(p.comments)) DESC")
    List<Post> findByAuthorIdSortByPopularityDesc(@Param("authorId") int authorId);

    @Query("SELECT p FROM Post p" +
            " WHERE p.author.id = :authorId" +
            " ORDER BY (COALESCE(p.likesCount, 0) + COALESCE(p.dislikesCount, 0) + SIZE(p.comments)) ASC")
    List<Post> findByAuthorIdSortByPopularityAsc(@Param("authorId") int authorId);
}
