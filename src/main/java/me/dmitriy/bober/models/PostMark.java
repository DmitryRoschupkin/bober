package me.dmitriy.bober.models;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "post_marks")
public class PostMark {

    @EmbeddedId
    private PostMarkId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("postId")
    @JoinColumn(name = "post_id")
    private Post post;

    @Column(name = "is_like", nullable = false)
    private boolean isLike;

}
