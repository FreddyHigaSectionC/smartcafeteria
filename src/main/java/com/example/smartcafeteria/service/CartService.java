package com.example.smartcafeteria.service;

import com.example.smartcafeteria.model.Cart;
import com.example.smartcafeteria.model.MenuItem;
import com.example.smartcafeteria.model.OrderItem;
import com.example.smartcafeteria.model.User;
import com.example.smartcafeteria.repository.CartRepository;
import com.example.smartcafeteria.repository.MenuItemRepository;
import com.example.smartcafeteria.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MenuItemRepository menuItemRepository;

    // Get cart by user, create if doesn't exist
    public Cart getCartByUser(User user) {
        return cartRepository.findByUser(user)
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setUser(user);
                    return cartRepository.save(newCart);
                });
    }

    // Remove an item from a user's cart
    public void removeItemFromCart(User user, Long orderItemId) {
        Cart cart = getCartByUser(user); // always returns a Cart

        OrderItem itemToRemove = cart.getItems().stream()
                .filter(item -> item.getId().equals(orderItemId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Item not found in cart"));

        cart.getItems().remove(itemToRemove);
        itemToRemove.setCart(null); // optional if orphanRemoval=true in CartItem mapping
        cartRepository.save(cart);
    }

    // Add item to the car
    public void addItemToCart(Cart cart, Long menuItemId, int quantity) {
        MenuItem menuItem = menuItemRepository.findById(menuItemId)
                .orElseThrow(() -> new RuntimeException("Menu item not found"));

        OrderItem orderItem = new OrderItem();
        orderItem.setMenuItem(menuItem);
        orderItem.setQuantity(quantity);
        orderItem.setPrice(menuItem.getPrice());
        orderItem.calculateTotal();
        orderItem.setCart(cart);

        cart.getItems().add(orderItem);
        cartRepository.save(cart);
    }

    // Clear a cart
    public void clearCart(Cart cart) {
        // Remove all items and set cart reference to null for each
        for (OrderItem item : cart.getItems()) {
            item.setCart(null);
        }
        cart.getItems().clear();
        cartRepository.save(cart);
    }

}
