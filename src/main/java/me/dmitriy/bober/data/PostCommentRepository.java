package me.dmitriy.bober.data;

import me.dmitriy.bober.models.PostComment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostCommentRepository extends JpaRepository<PostComment, Integer> {

}
