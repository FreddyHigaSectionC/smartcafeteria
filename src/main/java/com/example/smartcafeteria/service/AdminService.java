package com.example.smartcafeteria.service;

import com.example.smartcafeteria.model.MenuItem;
import com.example.smartcafeteria.model.Order;
import com.example.smartcafeteria.model.User;
import com.example.smartcafeteria.repository.MenuItemRepository;
import com.example.smartcafeteria.repository.OrderRepository;
import com.example.smartcafeteria.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AdminService {

    @Autowired
    private UserRepository userRepository;
    private OrderRepository orderRepository;
    private MenuItemRepository menuItemRepository;

    // ----------------------
    // USER MANAGEMENT
    // ----------------------

    // Get all users
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // Get a user by ID
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    // Update user
    public User updateUser(Long id, User updatedUser) {
        User user = userRepository.findById(id)
                        .orElseThrow(() -> new RuntimeException("User not found"));

        user.setFirst_name(updatedUser.getFirst_name());
        user.setLast_name(updatedUser.getLast_name());
        user.setEmail(updatedUser.getEmail());
        user.setRole(updatedUser.getRole());
        user.setActive(updatedUser.getActive());

        return userRepository.save(user);
    }

    // ----------------------
    // ORDER MANAGEMENT
    // ----------------------

    // Get current order
    public List<Order> getCurrentOrder() {
        return orderRepository.findAllByOrderByOrderTime();
    }

    // ----------------------
    // MENU MANAGEMENT
    // ----------------------

    public List<MenuItem> getAllMenuItems() {
        return menuItemRepository.findAll();
    }
}
