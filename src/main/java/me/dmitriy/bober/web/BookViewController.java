package me.dmitriy.bober.web;

import me.dmitriy.bober.data.AuthorRepository;
import me.dmitriy.bober.data.BookRepository;
import me.dmitriy.bober.data.CommentRepository;
import me.dmitriy.bober.data.MarkRepository;
import me.dmitriy.bober.models.*;
import me.dmitriy.bober.service.BookService;
import me.dmitriy.bober.service.CommentService;
import me.dmitriy.bober.service.UserService;
import me.dmitriy.bober.storage.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.core.io.Resource;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;


@Controller
@RequestMapping("/books")
public class BookViewController {

    private final BookRepository bookRepository;
    private final FileStorageService fileStorageService;
    private final UserService userService;
    private final AuthorRepository authorRepository;
    private final MarkRepository markRepository;
    private final BookService bookService;
    private final CommentRepository commentRepository;
    private final CommentService commentService;


    public BookViewController(BookRepository bookRepository, FileStorageService fileStorageService, UserService userService, AuthorRepository authorRepository, MarkRepository markRepository, BookService bookService, CommentRepository commentRepository, CommentService commentService) {
        this.bookRepository = bookRepository;
        this.fileStorageService = fileStorageService;
        this.userService = userService;
        this.authorRepository = authorRepository;
        this.markRepository = markRepository;
        this.bookService = bookService;
        this.commentRepository = commentRepository;
        this.commentService = commentService;
    }

    @GetMapping("/{id}")
    public String getBook(Model model, @PathVariable int id) {
        Book book = bookRepository.findByIdWithAuthors(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found"));

        User currentUser = null;
        Author author = null;
        String userMark = null;

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if(auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            currentUser = userService.getCurrentUser();
            if(currentUser != null) {
                author = authorRepository.findByUserId(currentUser.getId()).orElse(null);
                userMark = markRepository.findByUserIdAndBookId(currentUser.getId(), book.getId())
                        .map(Mark::getMark)
                        .orElse(null);
            }
        }

        List<Comment> flat = commentRepository.findByBookIdOrderByCreatedAtDesc(book.getId());

        Set<Integer> bookAuthorUserIds = book.getAuthors().stream()
                .map(Author::getUser)
                .filter(Objects::nonNull)
                .map(User::getId)
                .collect(Collectors.toSet());

        model.addAttribute("book", book);
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("author", author);
        model.addAttribute("userMark", userMark);
        model.addAttribute("comments", commentService.buildTree(flat));
        model.addAttribute("bookAuthorUserIds", bookAuthorUserIds);
        return "book-page";
    }

    @GetMapping("/{id}/download")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Resource> downloadBook(@PathVariable int id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found"));
        Resource fileResource = fileStorageService.loadAsResource(book.getFilePath());

        if(!fileResource.exists() || !fileResource.isReadable()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "File not found");
        }

        String downloadFilename = book.getTitle() + "." + book.getFileFormat();
        String encodedFilename = URLEncoder.encode(downloadFilename, StandardCharsets.UTF_8)
                .replace("+", "%20");

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + encodedFilename)
                .body(fileResource);
    }

    @PostMapping("{id}/mark")
    @PreAuthorize("isAuthenticated()")
    public String markBook(@PathVariable int id, @RequestParam String type) {
        User currentUser = userService.getCurrentUser();
        bookService.toggleMark(id, currentUser.getId(), type);
        return "redirect:/books/" + id;
    }

    @PostMapping("/{id}/comments")
    @PreAuthorize("isAuthenticated()")
    public String addComment(@PathVariable int id,
                             @RequestParam String text,
                             @RequestParam(required = false) Integer parentId) {
        commentService.addComment(id, parentId, text);
        return "redirect:/books/" + id + "#comments";
    }

    @PostMapping("/{commentId}/comments/delete")
    @PreAuthorize("isAuthenticated()")
    public String deleteComment(@PathVariable int commentId, @RequestParam int bookId) {
        commentService.softDelete(commentId);
        return "redirect:/books/" + bookId + "#comments";
    }
}
