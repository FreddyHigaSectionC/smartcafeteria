package com.example.smartcafeteria.controller;

import com.example.smartcafeteria.model.User;
import com.example.smartcafeteria.repository.UserRepository;
import com.example.smartcafeteria.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Controller
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    // Get user by ID
    @GetMapping("/{id}")
    public String getUserProfile(@PathVariable Long id, Model model) {
        User user = userService.getUserById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        model.addAttribute("user", user);
        return "user_profile";
    }

    // Get user id to edit
    @GetMapping("/edit/{id}")
    public String editUser(@PathVariable Long id, Model model, Principal principal) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        model.addAttribute("user", user);
        model.addAttribute("username", principal.getName());
        return "update_user";
    }

    // Update user
    @PostMapping("/update/{id}")
    public String updateUser(
            @PathVariable Long id,
            @ModelAttribute("user") User updatedUser
    ) {
        userService.updateUser(id, updatedUser);
        return "redirect:/home";
    }

    // Deactivate user account
    @PostMapping("/deactivate")
    public String deactivateSelf(Principal principal) {
        User user = userService.findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        userService.deactivateUser(user.getId());
        return "redirect:/login?deactivated";
    }
}
