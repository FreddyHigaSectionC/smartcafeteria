package com.example.smartcafeteria.service;

import com.example.smartcafeteria.model.MenuItem;
import com.example.smartcafeteria.repository.MenuItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@Service
public class MenuItemService {

    @Autowired
    private MenuItemRepository menuItemRepository;

    // Get all menus
    public List<MenuItem> getAllMenuItems() {
        return menuItemRepository.findAll();
    }

    // Add a menu
    public MenuItem addMenuItem(MenuItem menuitem) {
        return menuItemRepository.save(menuitem);
    }

    // Get menu by id
    public MenuItem getById(Long id) {
        return menuItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Menu item not found"));
    }

    // Update a menu
    public void updateMenuItem(Long id, MenuItem updatedData, MultipartFile imageFile) throws IOException {

        MenuItem existing = menuItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Menu item not found"));

        // Update fields
        existing.setName(updatedData.getName());
        existing.setPrice(updatedData.getPrice());
        existing.setDescription(updatedData.getDescription());
        existing.setCategory(updatedData.getCategory());
        existing.setAvailable(updatedData.getAvailable());

        // Update image only if a new one is provided
        if (imageFile != null && !imageFile.isEmpty()) {
            String fileName = UUID.randomUUID() + "_" + imageFile.getOriginalFilename();

            Path uploadPath = Paths.get("uploads/");
            Files.createDirectories(uploadPath);

            Path filePath = uploadPath.resolve(fileName);
            Files.copy(imageFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            existing.setImagePath("/uploads/" + fileName);
        }

        menuItemRepository.save(existing);
    }

}
