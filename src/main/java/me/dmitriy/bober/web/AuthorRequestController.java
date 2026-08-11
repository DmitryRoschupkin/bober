package me.dmitriy.bober.web;

import me.dmitriy.bober.service.AuthorRequestService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/become-author")
public class AuthorRequestController {

    private final AuthorRequestService authorRequestService;

    public AuthorRequestController(AuthorRequestService authorRequestService) {
        this.authorRequestService = authorRequestService;
    }

    @GetMapping
    public String showForm() {
        return "become-author";
    }

    @PostMapping
    public String submit(@RequestParam String resume) {
        authorRequestService.submit(resume);
        return "redirect:/account";
    }
}