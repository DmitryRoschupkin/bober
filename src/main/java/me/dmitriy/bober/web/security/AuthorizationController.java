package me.dmitriy.bober.web.security;


import me.dmitriy.bober.models.User;
import me.dmitriy.bober.models.UserRole;
import me.dmitriy.bober.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthorizationController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public AuthorizationController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/login")
    public String getLoginPage() {
        return "login-page";
    }

    @GetMapping("/registration")
    public String getRegistrationPage() {
        return "registration-page";
    }

    @PostMapping("/registration")
    public String createUser(@RequestParam String nickname,
                             @RequestParam String email,
                             @RequestParam String password) {
        String encodedPassword = passwordEncoder.encode(password);
        User user = new User(nickname, email, encodedPassword);
        userService.save(user);
        return "redirect:/login";
    }
}
