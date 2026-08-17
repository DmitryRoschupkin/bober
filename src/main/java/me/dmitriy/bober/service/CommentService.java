package me.dmitriy.bober.service;

import jakarta.transaction.Transactional;
import me.dmitriy.bober.data.BookRepository;
import me.dmitriy.bober.data.CommentRepository;
import me.dmitriy.bober.models.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final BookRepository bookRepository;
    private final UserService userService;

    public CommentService(CommentRepository commentRepository, BookRepository bookRepository, UserService userService) {
        this.commentRepository = commentRepository;
        this.bookRepository = bookRepository;
        this.userService = userService;
    }

    public List<CommentNode> buildTree(List<Comment> flatComments) {
        Map<Integer, CommentNode> nodesById = new LinkedHashMap<>();

        for (Comment comment : flatComments) {
            nodesById.put(comment.getId(), new CommentNode(comment));
        }

        List<CommentNode> roots = new ArrayList<>();
        for (CommentNode node : nodesById.values()) {
            Integer parentId = node.getComment().getParentId();
            CommentNode parent = parentId == null ? null : nodesById.get(parentId);
            if (parent != null) {
                parent.getReplies().add(node);
            } else  {
                roots.add(node);
            }
        }
        return roots;
    }

    @Transactional
    public void addComment(int bookId, Integer parentId, String text) {
        if(text == null || text.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Комментарий не может быть пустым");
        }

        Book book = bookRepository
                .findById(bookId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found"));

        if(parentId != null) {
            Comment parent = commentRepository
                    .findById(parentId)
                    .orElseThrow(() ->
                            new ResponseStatusException(HttpStatus.NOT_FOUND, "Родительский комментарий не найден"));
            if (parent.getBook().getId() != bookId) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Родительский комментарий принадлежит другой книге");
            }
        }

        User currentUser = userService.getCurrentUser();

        Comment comment = new Comment();
        comment.setBook(book);
        comment.setUser(currentUser);
        comment.setText(text.trim());
        comment.setParentId(parentId);
        comment.setCreatedAt(LocalDateTime.now());
        comment.setDeleted(false);

        commentRepository.save(comment);
    }

    public List<CommentNode> getTree(int bookId) {
        List<Comment> flat = commentRepository.findByBookIdOrderByCreatedAtDesc(bookId);
        return buildTree(flat);
    }

    @Transactional
    public void softDelete(int commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Comment not found"));

        User currentUser = userService.getCurrentUser();
        boolean isOwner = comment.getUser() != null && comment.getUser().getId() == currentUser.getId();
        boolean isStaff = UserRole.ADMIN.name().equals(currentUser.getRole())
                || UserRole.SUDO.name().equals(currentUser.getRole());

        if(!isOwner && !isStaff) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Попридержи коней, не дорос чужие комментарии удалять");
        }

        comment.setDeleted(true);
        comment.setText("[удалённый комментарий]");
    }
}
