package me.dmitriy.bober.models;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;

@Data
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

    public String getWordCase(){
        int amount = getBooksAmount();
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

    @OneToMany(mappedBy = "author", fetch = FetchType.LAZY)
    private List<Subscription> subscriptions;
}
