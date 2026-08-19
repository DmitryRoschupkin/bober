package me.dmitriy.bober.web;

import me.dmitriy.bober.data.AuthorRepository;
import me.dmitriy.bober.data.AuthorRequestRepository;
import me.dmitriy.bober.data.BookRepository;
import me.dmitriy.bober.data.UserRepository;
import me.dmitriy.bober.service.AuthorRequestService;
import me.dmitriy.bober.service.AuthorService;
import me.dmitriy.bober.service.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
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
    private final AuthorRequestRepository authorRequestRepository;
    private final AuthorRequestService authorRequestService;
    private final AuthorService authorService;

    public AdminController(UserRepository userRepository, UserService userService, AuthorRepository authorRepository, BookRepository bookRepository, AuthorRequestRepository authorRequestRepository, AuthorRequestService authorRequestService, AuthorService authorService) {
        this.userRepository = userRepository;
        this.userService = userService;
        this.authorRepository = authorRepository;
        this.bookRepository = bookRepository;
        this.authorRequestRepository = authorRequestRepository;
        this.authorRequestService = authorRequestService;
        this.authorService = authorService;
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SUDO')")
    @GetMapping
    public String getAdminPage(Model model) {
        model.addAttribute("users", userRepository.findAll());
        model.addAttribute("books", bookRepository.findAll());
        model.addAttribute("authors", authorRepository.findAll());
        model.addAttribute("authorRequests", authorRequestRepository.findAll());
        return "admin/management-page";
    }

    @PreAuthorize("hasRole('SUDO')")
    @PostMapping("/users/{id}/delete")
    public String deleteUser(@PathVariable int id) {
        userService.delete(id);
        return "redirect:/admin";
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SUDO')")
    @PostMapping("/users/{id}/block")
    public String blockUser(@PathVariable int id) {
        userService.setBlocked(id, true);
        return "redirect:/admin";
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SUDO')")
    @PostMapping("/users/{id}/unblock")
    public String unblockUser(@PathVariable int id) {
        userService.setBlocked(id, false);
        return "redirect:/admin";
    }

    @PreAuthorize("hasRole('SUDO')")
    @PostMapping("/users/promote")
    public String promoteUser(@RequestParam int id) {
        userService.promoteToAdmin(id);
        return "redirect:/admin";
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SUDO')")
    @PostMapping("/authors/{id}/delete")
    public String deleteAuthor(@PathVariable int id) {
        authorRepository.deleteById(id);
        return "redirect:/admin";
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SUDO')")
    @PostMapping("/books/{id}/delete")
    public String deleteBook(@PathVariable int id) {
        bookRepository.deleteById(id);
        return "redirect:/admin";
    }

    @PreAuthorize("hasAnyRole('SUDO', 'ADMIN')")
    @PostMapping("/author-requests/{id}/approve")
    public String approveAuthorRequest(@PathVariable int id) {
        authorRequestService.approve(id);
        return "redirect:/admin";
    }

    @PreAuthorize("hasAnyRole('SUDO', 'ADMIN')")
    @PostMapping("/author-requests/{id}/reject")
    public String rejectAuthorRequest(@PathVariable int id) {
        authorRequestService.reject(id);
        return "redirect:/admin";
    }

    @PreAuthorize("hasAnyRole('SUDO', 'ADMIN')")
    @PostMapping("/authors/link-with-coauthor")
    public String linkWithCoauthor(@RequestParam int authorId, @RequestParam String coauthor) {
        authorService.coauthorToAuthorConnect(authorId, coauthor);
        return "redirect:/admin";
    }
}
