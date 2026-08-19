package me.dmitriy.bober.data;

import me.dmitriy.bober.models.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Integer> {

}
