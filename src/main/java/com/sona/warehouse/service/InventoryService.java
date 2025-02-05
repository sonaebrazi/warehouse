package com.sona.warehouse.service;

import com.sona.warehouse.model.Inventory;
import com.sona.warehouse.repository.InventoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class InventoryService {

    private final InventoryRepository inventoryRepository;

    @Autowired
    public InventoryService(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    public void saveAll(List<Inventory> inventories) {
        for (Inventory inventory : inventories) {
            Optional<Inventory> existingInventory = inventoryRepository.findById(inventory.getId());

            if (existingInventory.isPresent()) {
                Inventory existing = existingInventory.get();
                existing.setStock(existing.getStock() + inventory.getStock());
                inventoryRepository.save(existing);
            } else {
                inventoryRepository.save(inventory);
            }
        }
    }
}
