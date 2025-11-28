package com.example.smartcafeteria.controller;

import com.example.smartcafeteria.model.User;
import com.example.smartcafeteria.repository.UserRepository;
import com.example.smartcafeteria.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;

@Controller
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserService userService;

    // root url - loads the landing page
    @GetMapping("/")
    public String index() {
        return "index";
    }
    // home page for authenticated users
    // retrieves user info using the logged0in principal
    @GetMapping("/home")
    public String homePage(Model model, Principal principal) {
        //fetch user data based on logged in username
        User user = userService.getUserByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        // add user indo to model for the home.html 
        model.addAttribute("username", principal.getName());
        model.addAttribute("userId", user.getId());

        return "home";
    }

    // login page 
    // displays error or logout messages when appropriate
    @GetMapping("/login")
    public String login(@RequestParam(value = "error", required = false) String error,
                        @RequestParam(value = "logout", required = false) String logout,
                        Model model) {
        if (error != null) model.addAttribute("error", true);
        if (logout != null) model.addAttribute("logout", true);
        return "login";
    }

    // loads the registration form
    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }
    // handles form submission for user registration
    // encodes password, assigns role, and saves user to db
    @PostMapping("/register")
    public String registerSubmit(@ModelAttribute User user) {
        // encrypt password before saving
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        // default role for new user
        user.setRole("ROLE_USER");
        //user.setRole("ROLE_ADMIN");
        // save new user record
        userRepository.save(user);
        // redirect to login page after successful
        return "redirect:/login";
    }

    // admin only page - requires role_admin access
    @GetMapping("/admin")
    public String admin() {
        return "admin";
    }
}
