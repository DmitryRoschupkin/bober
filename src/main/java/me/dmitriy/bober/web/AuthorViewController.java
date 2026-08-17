package me.dmitriy.bober.web;

import me.dmitriy.bober.data.AuthorRepository;
import me.dmitriy.bober.models.Author;
import me.dmitriy.bober.models.User;
import me.dmitriy.bober.service.SubscriptionService;
import me.dmitriy.bober.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@Controller
@RequestMapping("/authors")
public class AuthorViewController {

    @Autowired
    private AuthorRepository authorRepository;

    private final SubscriptionService subscriptionService;
    private final UserService userService;
    public AuthorViewController(SubscriptionService subscriptionService, UserService userService) {
        this.subscriptionService = subscriptionService;
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public String getAuthor(Model model, @PathVariable int id) {
        Author author = authorRepository
                .findByIdWithBooks(id)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Author not found"));
        User currentUser = userService.getCurrentUser();
        int subscribersCount = author.getSubscriptions().size();
        int booksAmount = author.getBooksAmount();
        boolean isSubscribed = subscriptionService.isSubscribed(currentUser, author);
        model.addAttribute("author", author);
        model.addAttribute("subscribersCount", subscribersCount);
        model.addAttribute("isSubscribed", isSubscribed);
        model.addAttribute("booksAmount", booksAmount);
        return "author-page";
    }

    @PostMapping("/{id}/subscribe")
    @PreAuthorize("isAuthenticated()")
    public String subscribe(@PathVariable int id) {
        Author author = authorRepository
                .findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Author not found"));
        User currentUser = userService.getCurrentUser();
        boolean isOwner = currentUser.getId() == author.getId();
        if (isOwner) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Нельзя подписаться на самого себя");
        } else {
            subscriptionService.toggleSubscription(currentUser, author);
        }
        return "redirect:/authors/" + id;
    }
}
