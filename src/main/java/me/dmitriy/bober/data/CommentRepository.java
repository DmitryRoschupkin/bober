package me.dmitriy.bober.data;


import me.dmitriy.bober.models.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Integer> {
    List<Comment> findByBookIdOrderByCreatedAtAsc(int bookId);
}
