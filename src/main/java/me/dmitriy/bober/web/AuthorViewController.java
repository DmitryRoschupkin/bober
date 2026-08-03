package me.dmitriy.bober.web;

import me.dmitriy.bober.data.AuthorRepository;
import me.dmitriy.bober.models.Author;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;

@Controller
@RequestMapping("/authors")
public class AuthorViewController {

    @Autowired
    private AuthorRepository authorRepository;

    @GetMapping("/{id}")
    public String getAuthor(Model model, @PathVariable int id) {
        Author author = authorRepository
                .findByIdWithBooks(id)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Author not found"));
        model.addAttribute("author", author);
        return "author-page";
    }
}
