package me.dmitriy.bober.service;

import jakarta.transaction.Transactional;
import me.dmitriy.bober.data.SubscriptionRepository;
import me.dmitriy.bober.models.Author;
import me.dmitriy.bober.models.Subscription;
import me.dmitriy.bober.models.User;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class SubscriptionService{

    SubscriptionRepository subscriptionRepository;
    public SubscriptionService(SubscriptionRepository subscriptionRepository) {
        this.subscriptionRepository = subscriptionRepository;
    }

    @Transactional
    public void toggleSubscription(User subscriber, Author author) {
        Optional<Subscription> existing = subscriptionRepository
                .findByUserIdAndAuthorId(subscriber.getId(), author.getId());
        if(existing.isPresent()) {
            Subscription subscription = existing.get();
            subscriptionRepository.delete(subscription);
        }else {
            Subscription subscription = new Subscription();
            subscription.setUser(subscriber);
            subscription.setAuthor(author);
            subscriptionRepository.save(subscription);
        }
    }

    public boolean isSubscribed(User subscriber, Author author) {
        boolean isSubscribed;
        Optional<Subscription> existing = subscriptionRepository.findByUserIdAndAuthorId(subscriber.getId(), author.getId());
        isSubscribed = existing.isPresent();
        return isSubscribed;
    }
}
