package com.pharmacore.pharmacore.view;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginView {

    @GetMapping("/login")
    public String login() {
        return "login";
    }
}
