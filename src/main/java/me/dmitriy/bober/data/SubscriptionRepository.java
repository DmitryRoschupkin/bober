package me.dmitriy.bober.data;

import me.dmitriy.bober.models.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SubscriptionRepository extends JpaRepository<Subscription, Integer> {
    Optional<Subscription> findByUserIdAndAuthorId(int user, int authorId);
//    long countByUserIdAndAuthorId(int user, int authorId);
}
