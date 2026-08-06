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
        String wordCase = "";
        if (amount == 0 || (amount > 4 && amount <= 20)) {
            wordCase = "книг";
        } else if ((amount % 10 == 1)) {
            wordCase = "книга";
        } else if ((amount % 10 >= 2) && (amount % 10 <= 4)) {
            wordCase = "книги";
        } else {
            wordCase = "книг";
        }
        return wordCase;
    }
}
