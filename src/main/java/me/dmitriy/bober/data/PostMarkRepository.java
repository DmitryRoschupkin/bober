package me.dmitriy.bober.data;

import me.dmitriy.bober.models.MarkId;
import me.dmitriy.bober.models.PostMark;
import me.dmitriy.bober.models.PostMarkId;
import me.dmitriy.bober.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostMarkRepository extends JpaRepository<PostMark, PostMarkId> {

    Optional<PostMark> findByUserIdAndPostId(Integer userId, Integer postId);
    void deleteByUserIdAndPostId(Integer userId, Integer postId);
}
