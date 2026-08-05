package me.dmitriy.bober.web;


import me.dmitriy.bober.data.BookRepository;
import me.dmitriy.bober.models.Book;
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

    public BooksController(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @GetMapping
    public String showBooks(Model model,
                            @RequestParam(value = "title",  required = false) String title,
                            @RequestParam(required = false, defaultValue = "all") String genre,
                            @RequestParam(required = false) Integer year,
                            @RequestParam(required = false) Integer authorId,
                            @RequestParam(required = false, defaultValue = "newest") String sort) {

        Sort sortOrder = switch (sort) {
            case "title" -> Sort.by("title").ascending();
            case "year" -> Sort.by("year").descending();
            default -> Sort.by("createdAt").descending();
        };

        String normalizedTitle = (title != null && !title.isBlank()) ? title.trim() : null;
        String genreFilter = "all".equals(genre) ? null : genre;
        List<Book> books = bookRepository.findFiltered(normalizedTitle, genreFilter, year, authorId, sortOrder);

        model.addAttribute("books", books);
        model.addAttribute("title", title);
        model.addAttribute("genre", genre);
        model.addAttribute("sort", sort);
        model.addAttribute("genres", bookRepository.findDistinctGenres());
        return "books-listing";
    }
}
