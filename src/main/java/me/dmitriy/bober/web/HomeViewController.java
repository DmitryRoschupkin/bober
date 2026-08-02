package me.dmitriy.bober.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class HomeViewController {

    @GetMapping
    public String index() {
        return "index";
    }
    @GetMapping("/home")
    public String home() {
        return "index";
    }
}
