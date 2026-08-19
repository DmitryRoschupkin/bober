package me.dmitriy.bober.data;

import me.dmitriy.bober.models.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PostMarkRepository extends JpaRepository<PostMark, PostMarkId> {

    Optional<PostMark> findByUserIdAndPostId(Integer userId, Integer postId);

    @Query(" SELECT pm FROM PostMark pm WHERE pm.user.id = :userId AND pm.post IN :posts")
    List<PostMark> findAllByUserIdAndPosts(@Param("userId") Integer userId, @Param("posts") List<Post> posts);

    @Query("SELECT COUNT(pm) FROM PostMark pm WHERE pm.post.id = :postId AND pm.isLike = :isLike")
    long countByPostIdAndIsLike(@Param("postId") Integer postId, @Param("isLike") boolean isLike);

    void deleteByUserIdAndPostId(Integer userId, Integer postId);
}
