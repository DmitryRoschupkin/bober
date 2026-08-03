package me.dmitriy.bober.web;


import me.dmitriy.bober.data.AuthorRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/authors")
public class AuthorsController {

    private AuthorRepository authorRepository;

    @GetMapping
    public String allAuthors(Model model) {
//        model.addAttribute(authorRepository.findAll());
        return "authors-listing";
    }
}
