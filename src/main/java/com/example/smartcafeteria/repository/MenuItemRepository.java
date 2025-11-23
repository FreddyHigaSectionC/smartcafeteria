package com.example.smartcafeteria.repository;

import com.example.smartcafeteria.model.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {
}
