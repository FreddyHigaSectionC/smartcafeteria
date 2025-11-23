package com.example.smartcafeteria.service;

import com.example.smartcafeteria.model.MenuItem;
import com.example.smartcafeteria.model.Order;
import com.example.smartcafeteria.model.OrderItem;
import com.example.smartcafeteria.model.User;
import com.example.smartcafeteria.repository.MenuItemRepository;
import com.example.smartcafeteria.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private MenuItemRepository menuItemRepository;

    public List<Order> getUserOrders(Long userId) {
        return orderRepository.findOrdersWithItemsByUserId(userId);
    }

    // Full Order object from JSON
    public Order placeOrder(Order order) {
        order.setStatus("PENDING");
        order.setOrderTime(LocalDateTime.now());
        return orderRepository.save(order);
    }

    public Order placeOrder(User user, Long menuItemId, int quantity) {

        // Fetch menu item
        MenuItem menuItem = menuItemRepository.findById(menuItemId)
                .orElseThrow(() -> new RuntimeException("Menu item not found"));

        // Create order
        Order order = new Order();
        order.setUser(user);
        order.setStatus("PENDING");
        order.setOrderTime(LocalDateTime.now());

        // Create order item
        OrderItem orderItem = new OrderItem();
        orderItem.setOrder(order);
        orderItem.setMenuItem(menuItem);
        orderItem.setQuantity(quantity);
        orderItem.setPrice(menuItem.getPrice());
        orderItem.setTotal(menuItem.getPrice() * quantity);

        // Attach to order
        order.setOrderItems(List.of(orderItem));

        // Set total for the order
        order.setTotal(orderItem.getTotal());

        // Save with cascade
        return orderRepository.save(order);
    }

    // Create order from cart
    public Order createOrder(User user, List<OrderItem> cartItems) {
        if (cartItems.isEmpty()) {
            throw new RuntimeException("Cart is empty, cannot create order");
        }

        Order order = new Order();
        order.setUser(user);
        order.setStatus("PENDING");
        order.setOrderTime(LocalDateTime.now());

        // Create new OrderItem objects for the order
        List<OrderItem> orderItems = new ArrayList<>();
        for (OrderItem cartItem : cartItems) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setMenuItem(cartItem.getMenuItem());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPrice(cartItem.getPrice());
            orderItem.calculateTotal();
            orderItems.add(orderItem);
        }

        order.setOrderItems(orderItems);

        // Calculate total
        order.calculateTotal();

        // Save order
        return orderRepository.save(order);
    }


    public List<Order> getPendingOrders() {
        return orderRepository.findByStatus("PENDING");
    }

    public void completeOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        order.setStatus("COMPLETED");
        orderRepository.save(order);
    }

    public Order cancelOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        if (!"PENDING".equals(order.getStatus()) ) {
            throw new RuntimeException("Cannot cancel completed or already cancelled order");
        }
        LocalDateTime now = LocalDateTime.now();
        if (now.isAfter(order.getOrderTime().plusMinutes(10))) {
            throw new RuntimeException("Cancel time limit exceeded");
        }
        order.setStatus("CANCELLED");
        return orderRepository.save(order);
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAllByOrderByOrderTime();
    }

    public List<Order> getUserOrdersWithItems(Long userId) {
        return orderRepository.findOrdersWithItemsByUserId(userId);
    }
}
