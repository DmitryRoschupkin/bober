package me.dmitriy.bober.models;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@ToString(exclude = {"books", "subscriptions", "user", "posts"})
@EqualsAndHashCode(exclude = {"books", "subscriptions", "user", "posts"})
@Entity
@Table(name = "author")
public class Author {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    private LocalDate birthDate;

    @Column(columnDefinition = "TEXT")
    private String bio;

    @ManyToMany(mappedBy = "authors")
    private List<Book> books;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    public Integer getAge() {
        if (birthDate == null) {
            return null;
        }
        return Period.between(birthDate, LocalDate.now()).getYears();
    }

    public int getBooksAmount() {
        return books == null ? 0 : books.size();
    }
    public int getSubscriptionsAmount() {
        return subscriptions == null ? 0 : subscriptions.size();
    }

    public String getWordCase(int amount){
        int lastTwoDigits = amount % 100;
        int lastDigit = amount % 10;

        if (lastTwoDigits >= 11 && lastTwoDigits <= 14) {
            return "книг";
        }
        if (lastDigit == 1) {
            return  "книга";
        }
        if (lastDigit >= 2 && lastDigit <= 4) {
            return "книги";
        }
        return "книг";
    }

    public String getSubsWordCase(int amount){
        int lastTwoDigits = amount % 100;
        int lastDigit = amount % 10;

        if (lastTwoDigits >= 11 && lastTwoDigits <= 14) {
            return "подписчиков";
        }
        if (lastDigit == 1) {
            return  "подписчик";
        }
        if (lastDigit >= 2 && lastDigit <= 4) {
            return "подписчика";
        }
        return "подписчиков";
    }

    @OneToMany(mappedBy = "author", fetch = FetchType.LAZY)
    private List<Subscription> subscriptions;

    @OneToMany(mappedBy = "author", fetch = FetchType.LAZY)
    private List<Post> posts;
}
