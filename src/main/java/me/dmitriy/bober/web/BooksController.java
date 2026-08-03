package me.dmitriy.bober.web;


import me.dmitriy.bober.data.BookRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/books")
public class BooksController {

    private BookRepository bookRepository;

    @GetMapping
    public String allBooks(Model model) {
//        model.addAttribute(bookRepository.findAll());
        return "books-listing";
    }
}
