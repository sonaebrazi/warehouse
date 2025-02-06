package com.sona.warehouse.repository;

import com.sona.warehouse.model.Inventory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@DataMongoTest
@ExtendWith(SpringExtension.class)
class InventoryRepositoryTest extends BaseRepositoryTest {

    @Autowired
    private InventoryRepository inventoryRepository;

    @Override
    protected void clearDatabase() {
        inventoryRepository.deleteAll();
    }

    @Test
    void saveAndFindArticle() {
        // Given an inventory item
        Inventory article = createInventoryItem("Leg", 12L);

        // When saving the item
        inventoryRepository.save(article);

        // Then it should be retrievable
        verifyInventoryItemRetrieval(article);
    }

    @Test
    void updateInventoryItem() {
        // Given an inventory item
        Inventory article = createInventoryItem("Screw", 15L);
        article = inventoryRepository.save(article);

        // When updating the item's stock
        article.setStock(20L);
        inventoryRepository.save(article);

        // Then the updated item should be retrievable
        Inventory updatedArticle = inventoryRepository.findById(article.getArticleId()).orElse(null);
        assertNotNull(updatedArticle);
        assertEquals(20, updatedArticle.getStock());
    }

    @Test
    void deleteInventoryItem() {
        // Given an inventory item
        Inventory article = createInventoryItem("Seat", 5L);
        article = inventoryRepository.save(article);

        // When deleting the item
        inventoryRepository.deleteById(article.getArticleId());

        // Then the item should not be present anymore
        assertFalse(inventoryRepository.findById(article.getArticleId()).isPresent());
    }

    @Test
    void findAllArticles() {
        // Given two inventory items
        Inventory article1 = createInventoryItem("Table Top", 10L);
        Inventory article2 = createInventoryItem("Screw", 15L);
        inventoryRepository.save(article1);
        inventoryRepository.save(article2);

        // When retrieving all items
        List<Inventory> articles = inventoryRepository.findAll();

        // Then the retrieved list should contain both items
        assertEquals(2, articles.size());
    }

    @Test
    void findNonExistentArticle() {
        // When trying to find a non-existent item
        Inventory foundArticle = inventoryRepository.findById("non-existent-id").orElse(null);

        // Then it should return null
        assertNull(foundArticle);
    }

    private Inventory createInventoryItem(String name, Long stock) {
        return Inventory.builder()
                .name(name)
                .stock(stock)
                .build();
    }

    private void verifyInventoryItemRetrieval(Inventory expectedArticle) {
        List<Inventory> articles = inventoryRepository.findAll();
        assertEquals(1, articles.size());
        assertEquals(expectedArticle.getName(), articles.get(0).getName());
        assertEquals(expectedArticle.getStock(), articles.get(0).getStock());
    }

}
