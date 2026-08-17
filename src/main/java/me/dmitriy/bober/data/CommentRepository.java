package me.dmitriy.bober.data;


import me.dmitriy.bober.models.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Integer> {
    @Query("SELECT c FROM Comment c JOIN FETCH c.user WHERE c.book.id = :bookId ORDER BY c.createdAt DESC")
    List<Comment> findByBookIdOrderByCreatedAtDesc(int bookId);
}
