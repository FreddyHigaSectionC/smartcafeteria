package com.example.smartcafeteria.controller;

import com.example.smartcafeteria.model.*;
import com.example.smartcafeteria.repository.OrderRepository;
import com.example.smartcafeteria.repository.UserRepository;
import com.example.smartcafeteria.service.CartService;
import com.example.smartcafeteria.service.MenuItemService;
import com.example.smartcafeteria.service.OrderService;
import com.example.smartcafeteria.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private MenuItemService menuItemService;

    @Autowired
    private UserService userService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CartService cartService;

    // Get all order
    @GetMapping("/all")
    public List<Order> getAllOrders() {
        return orderService.getAllOrders();
    }

    // get order by user id
    @GetMapping("/{userId}")
    public List<Order> getUserOrders(@PathVariable Long userId) {
        return orderService.getUserOrders(userId);
    }

    @GetMapping("/my_orders")
    public String myOrders(Model model, Principal principal) {
        User user = userService.findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Fetch orders with items
        List<Order> orders = orderService.getUserOrdersWithItems(user.getId());

        for (Order order : orders) {
            order.calculateTotal(); // sums all item totals
        }

        model.addAttribute("orders", orders);

        model.addAttribute("username", principal.getName());

        if (orders.isEmpty()) {
            model.addAttribute("message", "No orders found.");
        }

        return "my_orders";
    }

    // Load order page for specific menu item
    @GetMapping("/create/{id}")
    public String createOrderPage(@PathVariable Long id, Model model, Principal principal) {
        MenuItem menuItem = menuItemService.getById(id);
        model.addAttribute("menuItem", menuItem);
        model.addAttribute("user", userService.findByUsername(principal.getName()));
        model.addAttribute("username", principal.getName());
        return "order_form";
    }

    @GetMapping("/cart")
    public String viewCart(Principal principal, Model model) {
        User user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Cart cart = cartService.getCartByUser(user);
        model.addAttribute("cartItems", cart.getItems());
        double total = cart.getItems().stream().mapToDouble(OrderItem::getTotal).sum();
        model.addAttribute("total", total);

        return "cart";
    }

    // User cancels an order
    @PostMapping("/cancel/{id}")
    public String cancelOrder(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            orderService.cancelOrder(id);
            redirectAttributes.addFlashAttribute("successMessage", "Order #" + id + " cancelled successfully.");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/orders/my_orders";
    }

    // Handle order submission
    @PostMapping("/submit")
    public String submitOrder(@RequestParam Long menuItemId,
                              @RequestParam int quantity,
                              Principal principal,
                              Model model) {

        User user = userService.findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Place the order and get the saved order
        Order order = orderService.placeOrder(user, menuItemId, quantity);

        // Add data to confirmation page
        model.addAttribute("order", order);
        model.addAttribute("menuItem", order.getOrderItems().get(0).getMenuItem());

        return "order_confirmation";
    }

    // Add items to the cart
    @PostMapping("/add_to_cart")
    public String addToCart(@RequestParam Long menuItemId,
                            @RequestParam int quantity,
                            Principal principal) {

        User user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Cart cart = cartService.getCartByUser(user);
        cartService.addItemToCart(cart, menuItemId, quantity);

        return "redirect:/orders/cart";
    }

    // Submit the order
    @PostMapping("/checkout")
    public String checkout(Principal principal, Model model) {
        User user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Cart cart = cartService.getCartByUser(user);

        if (cart.getItems().isEmpty()) {
            model.addAttribute("errorMessage", "Cart is empty. Please add items before checkout.");
            return "cart";
        }

        Order order = orderService.createOrder(user, cart.getItems());
        cartService.clearCart(cart); // clear items after checkout

        model.addAttribute("order", order);
        return "order_confirmation";
    }

    // Remove item from cart
    @PostMapping("/cart/remove/{itemId}")
    public String removeFromCart(@PathVariable Long itemId, Principal principal) {
        User user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        cartService.removeItemFromCart(user, itemId);
        return "redirect:/orders/cart";
    }

}
