package com.example.smartcafeteria.repository;

import com.example.smartcafeteria.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUserId(Long userId);
    List<Order> findByStatus(String status);
    List<Order> findAllByOrderByOrderTime();

    // custom JPQL query to fetch orders including their order items in one query
    // left join fetch prevents the "n+1 select problem" by loading order + orderItems together
    // distinct ensures no duplicate order objects are returned when multiple items exist
    @Query("SELECT DISTINCT o FROM Order o LEFT JOIN FETCH o.orderItems WHERE o.user.id = :userId")
    List<Order> findOrdersWithItemsByUserId(@Param("userId") Long userId);
}
