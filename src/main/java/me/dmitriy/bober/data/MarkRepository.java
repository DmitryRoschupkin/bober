package me.dmitriy.bober.data;

import me.dmitriy.bober.models.Mark;
import me.dmitriy.bober.models.MarkId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MarkRepository extends JpaRepository<Mark, MarkId> {

    Optional<Mark> findByUserIdAndBookId(int userId, int bookId);
    long countByBookIdAndMark(int bookId, String mark);
}
