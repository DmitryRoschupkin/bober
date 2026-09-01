package me.dmitriy.bober.models;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

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

    @Column(name = "post_text",  nullable = false)
    private String text;

    @Column(name = "photo_path")
    private String photoPath;

    @Column(name = "likes_count")
    private int likesCount;

    @Column(name = "dislikes_count")
    private int dislikesCount;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "post", fetch = FetchType.LAZY)
    private List<PostComment> comments;

    public int getCommentsAmount() {
        return comments != null ? comments.size() : 0;
    }
}
