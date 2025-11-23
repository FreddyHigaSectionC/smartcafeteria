package com.example.smartcafeteria.controller;

import com.example.smartcafeteria.model.MenuItem;
import com.example.smartcafeteria.repository.MenuItemRepository;
import com.example.smartcafeteria.service.MenuItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.UUID;

@Controller
@RequestMapping("/menu")
public class MenuController {

    @Autowired
    private MenuItemService menuItemService;

    @Autowired
    private MenuItemRepository menuItemRepository;

    @GetMapping
    public String listMenu(Model model, Principal principal) {
        model.addAttribute("menuItems", menuItemService.getAllMenuItems());
        model.addAttribute("username", principal.getName());
        return "menu";
    }

    @GetMapping("/edit/{id}")
    public String editMenuItem(@PathVariable Long id, Model model) {
        MenuItem menuItem = menuItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Item not found"));
        model.addAttribute("menuItem", menuItem);
        return "edit_menu";
    }

    @PutMapping("/edit/{id}")
    public String updateMenuItem(
            @PathVariable Long id,
            @ModelAttribute MenuItem updatedData,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile) throws IOException {

        MenuItem existing = menuItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Menu item not found"));

        // Update fields
        existing.setName(updatedData.getName());
        existing.setPrice(updatedData.getPrice());
        existing.setDescription(updatedData.getDescription());
        existing.setCategory(updatedData.getCategory());
        existing.setAvailable(updatedData.getAvailable());

        // Update image only if a new one is uploaded
        if (imageFile != null && !imageFile.isEmpty()) {
            String fileName = UUID.randomUUID() + "_" + imageFile.getOriginalFilename();
            Path uploadPath = Paths.get("uploads/");
            Files.createDirectories(uploadPath);
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(imageFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            existing.setImagePath("/uploads/" + fileName);
        }

        // Save updated menu item
        menuItemService.updateMenuItem(id, updatedData, imageFile);
        return "redirect:/admin/menu";
    }
}
