package me.dmitriy.bober.models;


import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "users")
@NoArgsConstructor(force = true)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String nickname;
    private String email;
    private String password;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    private String bio;
    private String role;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "is_blocked")
    private boolean blocked;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<Subscription> subscriptions;

    public User(String nickname, String email, String password) {
        this.nickname = nickname;
        this.email = email;
        this.password = password;
        this.role = "USER";
        this.createdAt = LocalDateTime.now();
    }

    public List<String> fieldNames() {
        List<String> fieldNames = new ArrayList<>();
        List<String> prohibitedToEdit = new ArrayList<>();
        prohibitedToEdit.add("fieldNames");
        prohibitedToEdit.add("id");
        prohibitedToEdit.add("password");
        prohibitedToEdit.add("role");
        prohibitedToEdit.add("createdAt");
        prohibitedToEdit.add("nickname");
        prohibitedToEdit.add("blocked");
        prohibitedToEdit.add("subscriptions");

        Field[] fields = this.getClass().getDeclaredFields();
        for (Field field : fields) {
            if (!prohibitedToEdit.contains(field.getName())) {
                fieldNames.add(field.getName());
            }
        }
        return fieldNames;
    }

    public Integer getAge() {
        if (birthDate == null) {
            return null;
        }
        return Period.between(birthDate, LocalDate.now()).getYears();
    }

}
