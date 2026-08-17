package me.dmitriy.bober.web;


import me.dmitriy.bober.data.AuthorRepository;
import me.dmitriy.bober.models.Author;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.JpaSort;
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
                              @RequestParam(required = false, defaultValue = "name") String sort,
                              @RequestParam(required = false, defaultValue = "desc") String dir) {

        String normalizedName = (name != null && !name.isBlank()) ? name.trim() : null;
        boolean isAsc = "asc".equalsIgnoreCase(dir);
        Sort.Direction direction = isAsc ? Sort.Direction.ASC : Sort.Direction.DESC;

        List<Author> authors;

        switch (sort) {
            case "books" -> {
                Sort sortOrder = Sort.by(direction, "bookCount");
                authors = authorRepository.findByNameOrderByBooks(normalizedName, sortOrder);
            }
            case "subscribers" -> {
                Sort sortOrder = Sort.by(direction, "subsCount");
                authors = authorRepository.findByNameOrderBySubscribers(normalizedName, sortOrder);
            }
            default -> {
                Sort sortOrder = isAsc
                        ? Sort.by("firstName").ascending().and(Sort.by("lastName").ascending())
                        : Sort.by("firstName").descending().and(Sort.by("lastName").descending());
                authors = authorRepository.findByName(normalizedName, sortOrder);
            }
        }

        String reverseDir = isAsc ? "desc" : "asc";

        model.addAttribute("authors", authors);
        model.addAttribute("requiredAuthors", name);
        model.addAttribute("sort", sort);
        model.addAttribute("dir", dir);
        model.addAttribute("reverseDir", reverseDir);
        return "authors-listing";
    }
}
