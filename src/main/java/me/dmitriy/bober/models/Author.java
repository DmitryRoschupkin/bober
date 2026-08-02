package me.dmitriy.bober.models;

import lombok.Data;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;

@Data
public class Author {

    private int id;
    private String firstName;
    private String lastName;
    private LocalDate birthDate;
    private int age;
    private List<String> books;
    private int booksAmount = books.size();

    public Integer getAge() {
        if (birthDate == null) {
            return null;
        }
        return Period.between(birthDate, LocalDate.now()).getYears();
    }
}
