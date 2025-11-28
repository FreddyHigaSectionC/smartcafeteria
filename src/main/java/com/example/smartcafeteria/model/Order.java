package com.example.smartcafeteria.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "orders") // map this entity to the "orders" table
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // many orders belong to one user
    // LAZY loading to avoid fetching user data unless needed
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private String status;

    private LocalDateTime orderTime;

    // an order contains multiple items
    // EAGER fetch because order items are displayed with every order
    // cascade ensures updates/deletes auto apply to items
    //orphanRemoval removes order items if detached from order
    @OneToMany(mappedBy = "order", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems;

    private double total;
    // calculates total price, rounds the value to 2 decimal values
    public void calculateTotal() {
        double sum = orderItems.stream()
                .mapToDouble(OrderItem::getTotal)
                .sum();
        this.total = Math.round(sum * 100.0) / 100.0;
    }
}
