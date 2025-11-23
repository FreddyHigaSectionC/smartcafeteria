package com.example.smartcafeteria.repository;

import com.example.smartcafeteria.model.Cart;
import com.example.smartcafeteria.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {

    // Find a cart by user
    Optional<Cart> findByUser(User user);
}
