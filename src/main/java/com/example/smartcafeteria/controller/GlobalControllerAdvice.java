package com.example.smartcafeteria.controller;

import com.example.smartcafeteria.model.User;
import com.example.smartcafeteria.service.UserService;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.security.Principal;

@ControllerAdvice
public class GlobalControllerAdvice {

    private final UserService userService;

    public GlobalControllerAdvice(UserService userService) {
        this.userService = userService;
    }

    @ModelAttribute
    public void addUserAttributes(Model model, Principal principal) {
        if (principal != null) {
            User user = userService.getUserByUsername(principal.getName()).orElse(null);
            if (user != null) {
                model.addAttribute("username", user.getUsername());
                model.addAttribute("userId", user.getId());
            }
        }
    }
}

