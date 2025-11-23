package com.example.smartcafeteria.controller;

import com.example.smartcafeteria.model.*;
import com.example.smartcafeteria.repository.UserRepository;
import com.example.smartcafeteria.service.CartService;
import com.example.smartcafeteria.service.MenuItemService;
import com.example.smartcafeteria.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/cart")
public class CartController {

    @ModelAttribute("cart")
    public List<OrderItem> cart() {
        return new ArrayList<>();
    }

    @Autowired
    private MenuItemService menuItemService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CartService cartService;

    @Autowired
    private OrderService orderService;

    @PostMapping("/add/{id}")
    public String addToCart(@PathVariable Long id,
                            @RequestParam("quantity") int quantity,
                            @ModelAttribute("cart") List<OrderItem> cart) {

        MenuItem item = menuItemService.getById(id);

        // Create OrderItem
        OrderItem orderItem = new OrderItem();
        orderItem.setMenuItem(item);
        orderItem.setQuantity(quantity);
        orderItem.setPrice(item.getPrice());
        orderItem.calculateTotal();

        // Add to cart
        cart.add(orderItem);

        return "redirect:/menu"; // go back to menu
    }

    @GetMapping
    public String viewCart(Principal principal, Model model) {
        User user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Cart cart = cartService.getCartByUser(user);
        model.addAttribute("cartItems", cart.getItems());
        double total = cart.getItems().stream().mapToDouble(OrderItem::getTotal).sum();
        model.addAttribute("total", total);

        return "cart";
    }

    @PostMapping("/checkout")
    public String checkout(Principal principal, Model model) {
        User user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Cart cart = cartService.getCartByUser(user);

        if (cart.getItems().isEmpty()) {
            model.addAttribute("errorMessage", "Cart is empty. Please add items before checkout.");
            return "cart";
        }

        // Calculate totals for safety
        cart.getItems().forEach(OrderItem::calculateTotal);

        Order order = orderService.createOrder(user, cart.getItems());
        cartService.clearCart(cart); // clear items after checkout

        model.addAttribute("order", order);
        return "order_confirmation";
    }

    @PostMapping("/remove/{itemId}")
    public String removeFromCart(@PathVariable Long itemId, Principal principal) {
        User user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        cartService.removeItemFromCart(user, itemId);
        return "redirect:/cart";
    }

}

