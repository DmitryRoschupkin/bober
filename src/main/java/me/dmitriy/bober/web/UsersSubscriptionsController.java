package me.dmitriy.bober.web;

import me.dmitriy.bober.data.AuthorRepository;
import me.dmitriy.bober.data.BookRepository;
import me.dmitriy.bober.models.Author;
import me.dmitriy.bober.models.Book;
import me.dmitriy.bober.models.Subscription;
import me.dmitriy.bober.models.User;
import me.dmitriy.bober.service.SubscriptionService;
import me.dmitriy.bober.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.*;

@Controller
@RequestMapping("/subscriptions")
public class UsersSubscriptionsController {

    private final SubscriptionService subscriptionService;
    private final UserService userService;
    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    public UsersSubscriptionsController(SubscriptionService subscriptionService, UserService userService, BookRepository bookRepository, AuthorRepository authorRepository) {
        this.subscriptionService = subscriptionService;
        this.userService = userService;
        this.bookRepository = bookRepository;
        this.authorRepository = authorRepository;
    }
    @GetMapping()
    @PreAuthorize("isAuthenticated()")
    public String showSubscriptions(Model model) {
        User currentUser = userService.getCurrentUser();
        LocalDateTime weekAgo = LocalDateTime.now().minusWeeks(1);
        Map<Author, List<Book>> recentBooksByAuthor = new HashMap<>();
        List<Author> subscribedAuthors = new ArrayList<>();
        for(Subscription subscription : currentUser.getSubscriptions()) {
            int authorId = subscription.getAuthor().getId();
            Author author = authorRepository
                    .findByIdWithBooks(authorId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
            subscribedAuthors.add(author);
            if(author.getBooks() != null) {
                List<Book> recentBooks = author.getBooks().stream()
                        .filter(b -> b.getCreatedAt() != null && b.getCreatedAt().isAfter(weekAgo))
                        .toList();
                if(!recentBooks.isEmpty()) {
                    recentBooksByAuthor.put(author, recentBooks);
                }
            }
        }
        model.addAttribute("recentBooksByAuthor", recentBooksByAuthor);
        model.addAttribute("subscribedAuthors", subscribedAuthors);
        return "subscriptions";
    }

    @PostMapping("/{id}/unsubscribe")
    @PreAuthorize("isAuthenticated()")
    public String unsubscribe(@PathVariable int id) {
        Author author = authorRepository
                .findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Author not found"));
        User currentUser = userService.getCurrentUser();
        subscriptionService.toggleSubscription(currentUser, author);
        return "redirect:/subscriptions";
    }
}
