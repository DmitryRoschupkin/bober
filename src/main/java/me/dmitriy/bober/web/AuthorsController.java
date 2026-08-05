package me.dmitriy.bober.web;


import me.dmitriy.bober.data.AuthorRepository;
import me.dmitriy.bober.models.Author;
import org.springframework.data.domain.Sort;
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
    public String showAuthors(Model model,
                              @RequestParam(value = "name", required = false) String name,
                              @RequestParam(required = false, defaultValue = "name") String sort) {

        String normalizedName = (name != null && !name.isBlank()) ? name.trim() : null;

        List<Author> authors = "books".equals(sort)
                ? authorRepository.findByNameOrderByBookCountDesc(normalizedName)
                : authorRepository.findByName(normalizedName, Sort.by("firstName").ascending());

        model.addAttribute("authors", authors);
        model.addAttribute("requiredAuthors", name);
        model.addAttribute("sort", sort);
        return "authors-listing";
    }
}
