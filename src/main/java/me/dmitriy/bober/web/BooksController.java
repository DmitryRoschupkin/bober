package me.dmitriy.bober.web;


import me.dmitriy.bober.data.BookRepository;
import me.dmitriy.bober.models.Book;
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
    public String showBooks(Model model, @RequestParam(value = "title",  required = false) String title) {
        List<Book> books;
        if (title != null && !title.isBlank()) {
            books = bookRepository.findByTitle("%"+title.trim()+"%");
        } else {
            books = bookRepository.findAll();
        }
        model.addAttribute("books", books);
        model.addAttribute("title", title);
        return "books-listing";
    }
}
