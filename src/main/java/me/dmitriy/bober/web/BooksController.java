package me.dmitriy.bober.web;


import me.dmitriy.bober.data.BookRepository;
import me.dmitriy.bober.data.CommentRepository;
import me.dmitriy.bober.models.Book;
import me.dmitriy.bober.models.User;
import me.dmitriy.bober.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/books")
public class BooksController {

    private final BookRepository bookRepository;
    private final UserService userService;

    public BooksController(BookRepository bookRepository, UserService userService) {
        this.bookRepository = bookRepository;
        this.userService = userService;
    }

    @GetMapping
    public String showBooks(Model model,
                            @RequestParam(value = "title",  required = false) String title,
                            @RequestParam(required = false, defaultValue = "all") String genre,
                            @RequestParam(required = false) Integer year,
                            @RequestParam(required = false) Integer authorId,
                            @RequestParam(required = false, defaultValue = "newest") String sort,
                            @RequestParam(defaultValue = "0") int page) {

        Sort sortOrder = switch (sort) {
            case "title" -> Sort.by("title").ascending();
            case "year" -> Sort.by("year").descending();
            default -> Sort.by("createdAt").descending();
        };

        Pageable pageable = PageRequest.of(page, 10, sortOrder);

        String normalizedTitle = (title != null && !title.isBlank()) ? title.trim() : null;
        String genreFilter = "all".equals(genre) ? null : genre;

        Page<Book> booksPage = bookRepository.findFiltered(normalizedTitle, genreFilter, year, authorId, pageable);

        model.addAttribute("books", booksPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", booksPage.getTotalPages());

        model.addAttribute("title", title);
        model.addAttribute("genre", genre);
        model.addAttribute("year", year);
        model.addAttribute("authorId", authorId);
        model.addAttribute("sort", sort);
        model.addAttribute("genres", bookRepository.findDistinctGenres());
        return "books-listing";
    }
}
