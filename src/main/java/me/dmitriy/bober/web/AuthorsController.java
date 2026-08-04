package me.dmitriy.bober.web;


import me.dmitriy.bober.data.AuthorRepository;
import me.dmitriy.bober.models.Author;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/authors")
public class AuthorsController {

    private final AuthorRepository authorRepository;

    public AuthorsController(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

    @GetMapping
    public String showAuthors(Model model, @RequestParam(value = "name", required = false) String name) {
        List<Author> authors;
        if (name != null && !name.isBlank()) {
            authors = authorRepository.findByName("%"+name.trim()+"%");
        } else {
            authors = authorRepository.findAll();
        }
        model.addAttribute("authors", authors);
        model.addAttribute("requiredAuthors", name);
        return "authors-listing";
    }
}
