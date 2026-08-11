package me.dmitriy.bober.data;

import me.dmitriy.bober.models.AuthorRequest;
import me.dmitriy.bober.models.AuthorRequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AuthorRequestRepository extends JpaRepository<AuthorRequest, Integer> {

    List<AuthorRequest> findByStatus(AuthorRequestStatus status);

    Optional<AuthorRequest> findByUserIdAndStatus(int userId, AuthorRequestStatus status);
}