package me.dmitriy.bober.models;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "author_requests")
public class AuthorRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String resume;

    @Enumerated(EnumType.STRING)
    private AuthorRequestStatus status = AuthorRequestStatus.PENDING;

    @Column(name = "created_at")
    private LocalDateTime createdAt =  LocalDateTime.now();

}
