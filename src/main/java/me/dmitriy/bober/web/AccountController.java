package me.dmitriy.bober.web;


import me.dmitriy.bober.data.UserRepository;
import me.dmitriy.bober.models.User;
import me.dmitriy.bober.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Controller
@RequestMapping("/account")
public class AccountController {

    private final UserRepository userRepository;
    private final UserService userService;
    @Autowired
    public AccountController(UserRepository userRepository, UserService userService) {
        this.userRepository = userRepository;
        this.userService = userService;
    }

    @GetMapping
    public String getCurrentUserAccount(Model model) {
        User currentUser = userService.getCurrentUser();
        populateModel(model, currentUser, currentUser);
        return "account";
    }


    @GetMapping("/{id}")
    public String getAccount(Model model, @PathVariable int id) {
        User user = userRepository
                .findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User "+id+" not found"));
        User currentUser = userService.getCurrentUser();
        populateModel(model, user, currentUser);
        return "account";
    }

    private void populateModel(Model model, User user, User currentUser) {
        String privilege = switch (user.getRole()) {
            case "USER" -> "Читатель";
            case "AUTHOR" -> "Автор";
            case "ADMIN" -> "Модератор";
            case "SUDO" -> "Админ";
            default -> throw new IllegalStateException("Unexpected value: " + user.getRole());
        };
        model.addAttribute("user", user);
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("privilege", privilege);
    }

}
