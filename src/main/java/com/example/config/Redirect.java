package com.example.config;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping
public class Redirect {
    @GetMapping({"/swagger", "/swagger/"})
    public String swagger() {
        return "redirect:/swagger-ui/index.html";
    }
}
