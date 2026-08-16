package me.dmitriy.bober.models;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;

import java.io.Serializable;

@Embeddable
@Data
public class MarkId implements Serializable {

    @Column(name = "user_id")
    private int userId;

    @Column(name = "book_id")
    private int bookId;

}
