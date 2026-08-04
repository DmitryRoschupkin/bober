package me.dmitriy.bober.web;

import me.dmitriy.bober.data.BookRepository;
import me.dmitriy.bober.models.Book;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;


@Controller
@RequestMapping("/books")
public class BookViewController {

    @Autowired
    private BookRepository bookRepository;

    @GetMapping("/{id}")
    public String getBook(Model model, @PathVariable int id) {
        Book book = bookRepository.findByIdWithAuthors(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found"));
        model.addAttribute("book", book);
        return "book-page";
    }
}
