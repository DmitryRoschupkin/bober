package me.dmitriy.bober.models;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "posts")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private Author author;

    @Column(name = "title")
    private String title;

    @Column(name = "post_text")
    private String text;

    @Column(name = "likes_count")
    private int likes_count;

    @Column(name = "dislikes_count")
    private int dislikes_count;

    @Column(name = "created_at")
    private LocalDateTime created_at;
}
