package me.dmitriy.bober.web;

import me.dmitriy.bober.data.AuthorRepository;
import me.dmitriy.bober.data.BookRepository;
import me.dmitriy.bober.data.UserRepository;
import me.dmitriy.bober.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserRepository userRepository;
    private final UserService userService;
    private final AuthorRepository authorRepository;
    private final BookRepository bookRepository;
    public AdminController(UserRepository userRepository, UserService userService, AuthorRepository authorRepository, BookRepository bookRepository) {
        this.userRepository = userRepository;
        this.userService = userService;
        this.authorRepository = authorRepository;
        this.bookRepository = bookRepository;
    }

    @GetMapping
    public String getAdminPage(Model model) {
        model.addAttribute("users", userRepository.findAll());
        model.addAttribute("books", bookRepository.findAll());
        model.addAttribute("authors", authorRepository.findAll());
        return "admin/management-page";
    }
}
