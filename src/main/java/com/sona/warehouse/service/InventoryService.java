package com.sona.warehouse.service;

import com.sona.warehouse.dto.InventoryArticleDTO;
import com.sona.warehouse.dto.InventoryDTO;
import com.sona.warehouse.model.Inventory;
import com.sona.warehouse.repository.InventoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Service class responsible for managing inventory operations.
 * This class handles the business logic related to inventory items,
 * including saving and updating inventory articles.
 */
@Service
public class InventoryService {

    private static final Logger logger = LoggerFactory.getLogger(InventoryService.class);
    private final InventoryRepository inventoryRepository;

    /**
     * Constructs an InventoryService with the specified InventoryRepository.
     *
     * @param inventoryRepository the repository for accessing inventory data.
     */
    @Autowired
    public InventoryService(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    /**
     * Saves all inventory articles provided in the InventoryDTO.
     * This method updates existing inventory articles or creates new ones
     * based on the provided inventory data.
     *
     * @param inventory the DTO containing a list of inventory articles to be saved.
     */
    public void saveAll(InventoryDTO inventory) {
        logger.info("Saving inventory with {} items", inventory.getInventory().size());

        for (InventoryArticleDTO articleDTO : inventory.getInventory()) {
            Optional<Inventory> existingInventory = inventoryRepository.findById(articleDTO.getArticleId());

            if (existingInventory.isPresent()) {
                Inventory existing = existingInventory.get();
                existing.setStock(existing.getStock() + Long.parseLong(articleDTO.getStock()));
                inventoryRepository.save(existing);
                logger.debug("Updated stock for articleId {}: new stock {}", articleDTO.getArticleId(), existing.getStock());
            } else {
                inventoryRepository.save(toModel(articleDTO));
                logger.info("Added new inventory item: {}", articleDTO.getArticleId());
            }
        }
    }

    /**
     * Converts an InventoryArticleDTO to an Inventory model.
     *
     * @param articleDTO the InventoryArticleDTO to be converted.
     * @return the corresponding Inventory model.
     */
    private Inventory toModel(InventoryArticleDTO articleDTO) {
        return Inventory.builder()
                .articleId(articleDTO.getArticleId())
                .stock(Long.parseLong(articleDTO.getStock()))
                .name(articleDTO.getName())
                .build();
    }
}
