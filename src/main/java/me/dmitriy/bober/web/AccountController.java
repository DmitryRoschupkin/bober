package me.dmitriy.bober.web;


import me.dmitriy.bober.data.UserRepository;
import me.dmitriy.bober.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Optional;

@Controller
@RequestMapping("/account")
public class AccountController {

    private final UserRepository userRepository;
    @Autowired
    public AccountController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/{id}")
    public String getAccount(Model model, @PathVariable int id) {
        Optional<User> user = userRepository.findById(id);
        model.addAttribute("user", user);
        return "account";
    }
}
