package me.dmitriy.bober.models;


import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "marks")
public class Mark {

    @EmbeddedId
    private MarkId id = new MarkId();

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("bookId")
    @JoinColumn(name = "book_id")
    private Book book;

    @Column(name = "mark",  nullable = false, length = 10)
    private String mark;
}
