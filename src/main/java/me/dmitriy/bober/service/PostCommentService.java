package me.dmitriy.bober.service;

import jakarta.transaction.Transactional;
import me.dmitriy.bober.data.PostCommentRepository;
import me.dmitriy.bober.data.PostRepository;
import me.dmitriy.bober.models.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PostCommentService {

    private final PostCommentRepository postCommentRepository;
    private final PostRepository postRepository;
    private final UserService userService;

    public PostCommentService(PostCommentRepository postCommentRepository, PostRepository postRepository, UserService userService) {
        this.postCommentRepository = postCommentRepository;
        this.postRepository = postRepository;
        this.userService = userService;
    }

    public List<PostCommentNode> buildTree(List<PostComment> flatPostComments) {
        Map<Integer, PostCommentNode> nodesById = new HashMap<>();

        for(PostComment postComment : flatPostComments) {
            nodesById.put(postComment.getId(), new PostCommentNode(postComment));
        }

        List<PostCommentNode> roots = new ArrayList<>();
        for (PostCommentNode node : nodesById.values()) {
            Integer parentId = node.getPostComment().getParentId();
            PostCommentNode parent = parentId == null ? null : nodesById.get(parentId);
            if(parent != null) {
                parent.getReplies().add(node);
            } else  {
                roots.add(node);
            }
        }
        return roots;
    }

    @Transactional
    public void addComment(int postId, Integer parentId, String text) {
        if(text == null || text.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Комментарий не может быть пустым!");
        }
        Post post = postRepository
                .findById(postId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "post not found"));
        if(parentId != null) {
            PostComment parent = postCommentRepository
                    .findById(parentId)
                    .orElseThrow(() ->
                            new ResponseStatusException(HttpStatus.NOT_FOUND, "Родительский комментарий не найден"));
            if (parent.getPost().getId() != postId) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Родительский комментарий принадлежит другому посту!");
            }
        }
        User currentUser = userService.getCurrentUser();
        PostComment postComment = new PostComment();
        postComment.setPost(post);
        postComment.setUser(currentUser);
        postComment.setText(text.trim());
        postComment.setParentId(parentId);
        postComment.setCreatedAt(LocalDateTime.now());
        postComment.setDeleted(false);
        postCommentRepository.save(postComment);
    }

    @Transactional
    public void softDelete(int commentId) {
        PostComment postComment = postCommentRepository
                .findById(commentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "post not found"));
        User currentUser = userService.getCurrentUser();
        boolean isOwner = postComment.getUser() != null && postComment.getUser().getId() == currentUser.getId();
        boolean isStaff = UserRole.ADMIN.name().equals(currentUser.getRole())
                || UserRole.SUDO.name().equals(currentUser.getRole());
        if(!isOwner && !isStaff) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Попридержи коней, не дорос чужие комментарии удалять");
        }
        postComment.setDeleted(true);
        postComment.setText("[удалённый комментарий]");
    }

    @Transactional
    public void editComment(int commentId, String text) {
        PostComment postComment = postCommentRepository
                .findById(commentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "post not found"));
        User currentUser = userService.getCurrentUser();
        boolean isOwner = postComment.getUser() != null && postComment.getUser().getId() == currentUser.getId();
        if(!isOwner) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Нельзя редактировать чужие комментарии! Ты не serotsvet03!");
        }
        if (text == null || text.isBlank()) {
            throw new IllegalArgumentException("Комментарий не может быть пустым. Придумай что-нибудь");
        }
        postComment.setText(text.trim());
        postComment.setUpdatedAt(LocalDateTime.now());
        postCommentRepository.save(postComment);
    }
}
