package me.dmitriy.bober.service;


import jakarta.transaction.Transactional;
import me.dmitriy.bober.data.PostMarkRepository;
import me.dmitriy.bober.data.PostRepository;
import me.dmitriy.bober.data.UserRepository;
import me.dmitriy.bober.models.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class PostService {

    private final PostRepository postRepository;
    private final PostMarkRepository postMarkRepository;
    private final UserRepository userRepository;

    public PostService(PostRepository postRepository, PostMarkRepository postMarkRepository, UserRepository userRepository) {
        this.postRepository = postRepository;
        this.postMarkRepository = postMarkRepository;
        this.userRepository = userRepository;
    }

    public void postMessage(Author author, String title, String text) {
        Post post = new Post();
        post.setAuthor(author);
        post.setTitle(title);
        post.setText(text);
        post.setCreatedAt(LocalDateTime.now());
        postRepository.save(post);
    }

    @Transactional
    public void toggleMark(int postId, int userId, boolean isLike) {
        Post post = postRepository
                .findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post with id " + postId + " does not exist"));

        Optional<PostMark> existing = postMarkRepository.findByUserIdAndPostId(userId, postId);

        if(existing.isPresent()) {
            PostMark existingMark = existing.get();
            if(existingMark.isLike() == isLike) {
                postMarkRepository.delete(existingMark);
            } else  {
                existingMark.setLike(isLike);
                postMarkRepository.save(existingMark);
            }
        } else {
            PostMark postMark = new PostMark();

            PostMarkId postMarkId = new PostMarkId();
            postMarkId.setPostId(postId);
            postMarkId.setUserId(userId);

            postMark.setId(postMarkId);
            postMark.setPost(post);
            postMark.setUser(userRepository.getReferenceById(userId));
            postMark.setLike(isLike);
            postMarkRepository.save(postMark);
        }

        postMarkRepository.flush();

        int likes = (int) postMarkRepository.countByPostIdAndIsLike(postId, true);
        int dislikes = (int) postMarkRepository.countByPostIdAndIsLike(postId, false);

        post.setLikesCount(likes);
        post.setDislikesCount(dislikes);
        postRepository.save(post);
    }
}
