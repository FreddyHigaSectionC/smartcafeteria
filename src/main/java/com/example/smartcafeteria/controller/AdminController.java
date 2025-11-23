package com.example.smartcafeteria.controller;

import com.example.smartcafeteria.model.MenuItem;
import com.example.smartcafeteria.model.Order;
import com.example.smartcafeteria.model.User;
import com.example.smartcafeteria.repository.MenuItemRepository;
import com.example.smartcafeteria.repository.UserRepository;
import com.example.smartcafeteria.service.AdminService;
import com.example.smartcafeteria.service.MenuItemService;
import com.example.smartcafeteria.service.OrderService;
import com.example.smartcafeteria.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private AdminService adminService;

    @Autowired
    private MenuItemService menuItemService;

    @Autowired
    private MenuItemRepository menuItemRepository;

    @Autowired
    private UserRepository userRepository;

    // ----------------------
    // USER
    // ----------------------

    // Get all users
    @GetMapping("/users")
    public String userManagement(Model model, Principal principal) {
        // Fetch all users from the service
        List<User> users = adminService.getAllUsers();
        // Pass the list to the Thymeleaf template
        model.addAttribute("users", users);
        model.addAttribute("username", principal.getName());
        return "admin_user_management";
    }

    // Get a user by id
    @GetMapping("/users/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        return userService.getUserById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Get user id to edit
    @GetMapping("/users/edit/{id}")
    public String editUser(@PathVariable Long id, Model model, Principal principal) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        model.addAttribute("user", user);
        model.addAttribute("username", principal.getName());
        return "admin_update_user";
    }

    // Update user
    @PostMapping("/users/update/{id}")
    public String updateUser(
            @PathVariable Long id,
            @ModelAttribute("user") User updatedUser
    ) {
        adminService.updateUser(id, updatedUser);
        return "redirect:/admin/users";
    }

    @PostMapping("/users/{id}/deactivate")
    public String deactivateUser(@PathVariable Long id) {

        userService.deactivateUser(id);

        return "redirect:/home";
    }

    // ----------------------
    // ORDER
    // ----------------------

    // Get all orders and admin username
    @GetMapping("/dashboard")
    public String adminDashboard(Model model, Principal principal) {

        List<Order> orders = orderService.getAllOrders();
        model.addAttribute("orders", orders);

        if (orders.isEmpty()) {
            model.addAttribute("emptyMessage", "No orders found.");
        }

        model.addAttribute("username", principal.getName());

        return "admin_dashboard";
    }

    // Get all orders
    @GetMapping("/orders")
    public List<Order> getAllOrders() {
        return orderService.getAllOrders();
    }

    // Mark an order as completed
    @PostMapping("/orders/complete/{id}")
    public String completeOrder(@PathVariable Long id) {
        orderService.completeOrder(id);
        return "redirect:/admin/dashboard";
    }

    // ----------------------
    // MENU
    // ----------------------

    // Get the menu
    @GetMapping("/menu")
    public String adminMenuList(Model model, Principal principal) {
        List<MenuItem> menuItems = menuItemService.getAllMenuItems();
        model.addAttribute("menuItems", menuItems);

        if (menuItems.isEmpty()) {
            model.addAttribute("emptyMessage", "No menu items found.");
        }

        model.addAttribute("username", principal.getName());
        return "admin_menu";
    }

    // Create a new menu
    @GetMapping("/menu/add")
    public String addForm(Model model, Principal principal) {
        model.addAttribute("menuItem", new MenuItem());
        model.addAttribute("username", principal.getName());
        return "admin_add_menu";
    }

    // Get menu id to edit
    @GetMapping("/menu/edit/{id}")
    public String editMenuItem(@PathVariable Long id, Model model, Principal principal) {
        MenuItem menuItem = menuItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Item not found"));
        model.addAttribute("menuItem", menuItem);
        model.addAttribute("username", principal.getName());
        return "admin_edit_menu";
    }

    // Create a new menu item and save it
    @PostMapping("/menu/update")
    public String addSubmit(@ModelAttribute MenuItem menuItem,
                            @RequestParam("imageFile") MultipartFile imageFile) throws IOException {

        if (!imageFile.isEmpty()) {
            String fileName = UUID.randomUUID() + "_" + imageFile.getOriginalFilename();
            Path uploadPath = Paths.get("uploads/");
            Files.createDirectories(uploadPath);
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(imageFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            menuItem.setImagePath("/uploads/" + fileName);
        }

        menuItemService.addMenuItem(menuItem);
        return "redirect:/admin/menu";
    }

    // Update the menu with the change
    @PostMapping("/menu/update/{id}")
    public String updateMenuItem(
            @PathVariable Long id,
            @ModelAttribute MenuItem updatedData,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile
    ) throws IOException {
        menuItemService.updateMenuItem(id, updatedData, imageFile);
        return "redirect:/admin/menu";
    }
}
