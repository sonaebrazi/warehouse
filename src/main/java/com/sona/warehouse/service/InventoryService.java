package com.sona.warehouse.service;

import com.sona.warehouse.dto.InventoryArticleDTO;
import com.sona.warehouse.dto.InventoryDTO;
import com.sona.warehouse.model.Inventory;
import com.sona.warehouse.repository.InventoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class InventoryService {

    private final InventoryRepository inventoryRepository;

    @Autowired
    public InventoryService(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    public void saveAll(InventoryDTO inventory) {
        for (InventoryArticleDTO articleDTO : inventory.getInventory()) {
            Optional<Inventory> existingInventory = inventoryRepository.findById(articleDTO.getArticleId());

            if (existingInventory.isPresent()) {
                Inventory existing = existingInventory.get();
                existing.setStock(existing.getStock() + Long.parseLong(articleDTO.getStock()));
                inventoryRepository.save(existing);
            } else {
                inventoryRepository.save(toModel(articleDTO));
            }
        }
    }

    private Inventory toModel(InventoryArticleDTO articleDTO) {
        return Inventory.builder()
                .articleId(articleDTO.getArticleId())
                .stock(Long.parseLong(articleDTO.getStock()))
                .name(articleDTO.getName())
                .build();
    }
}
