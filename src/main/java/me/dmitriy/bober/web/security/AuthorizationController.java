package me.dmitriy.bober.web.security;


import me.dmitriy.bober.models.User;
import me.dmitriy.bober.models.UserRole;
import me.dmitriy.bober.service.UserService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Controller
public class AuthorizationController {

    //Smith is here!
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public AuthorizationController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/login")
    public String getLoginPage(Model model, @RequestParam(required = false) String error) {
        if (error != null) {
            model.addAttribute("isAuthenticationFailed", error);
        }
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
        int id = user.getId();
        forceAutoLogin(nickname, encodedPassword);
        return "redirect:/account/"+id;
    }

    private void forceAutoLogin(String nickname, String password) {
        Set<SimpleGrantedAuthority> roles = Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"));
        Authentication auth = new UsernamePasswordAuthenticationToken(nickname, password, roles);
        SecurityContextHolder.getContext().setAuthentication(auth);
    }
}
