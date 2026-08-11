package me.dmitriy.bober.web;

import me.dmitriy.bober.data.UserRepository;
import me.dmitriy.bober.models.User;
import me.dmitriy.bober.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/account")
public class AccountEditController {

    private final UserService userService;
    UserRepository userRepository;
    public AccountEditController(UserRepository userRepository, UserService userService) {
        this.userRepository = userRepository;
        this.userService = userService;
    }

    @GetMapping("/edit/{id}")
    public String editAccountForm(Model model, @PathVariable int id) {
        User currentUser = userService.getCurrentUser();
        User user = userRepository
                .findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User with id " + id + " not found"));
        List<String> fieldNames = user.fieldNames();
        model.addAttribute("user", user);
        model.addAttribute("fieldNames", fieldNames);
        model.addAttribute("currentUser", currentUser);
        return "account-edit";
    }

    @PostMapping("/edit/{id}")
    public String editAccountProcess(@PathVariable int id, @ModelAttribute User user) {
        userService.update(user, id);
        return "redirect:/account/"+id;
    }
}
