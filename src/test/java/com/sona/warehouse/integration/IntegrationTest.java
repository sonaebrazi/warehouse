package com.sona.warehouse.integration;

import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.PortBinding;
import com.github.dockerjava.api.model.Ports;
import com.sona.warehouse.dto.ProductArticleDTO;
import com.sona.warehouse.dto.ProductDTO;
import com.sona.warehouse.exceptions.ProductNotFoundException;
import com.sona.warehouse.exceptions.ProductSoldOutException;
import com.sona.warehouse.model.Inventory;
import com.sona.warehouse.model.Product;
import com.sona.warehouse.repository.InventoryRepository;
import com.sona.warehouse.repository.ProductRepository;
import com.sona.warehouse.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Testcontainers
@SpringBootTest
@ExtendWith(SpringExtension.class)
class IntegrationTest {

    private static final int MONGODB_PORT = 27017;

    @Container
    private static final MongoDBContainer mongoDBContainer = createMongoDBContainer();

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private InventoryRepository inventoryRepository;

    private static MongoDBContainer createMongoDBContainer() {
        return new MongoDBContainer(DockerImageName.parse("mongo:latest"))
                .withExposedPorts(MONGODB_PORT)
                .withCreateContainerCmdModifier(cmd -> cmd.withPortBindings(
                        new PortBinding(Ports.Binding.bindPort(MONGODB_PORT), new ExposedPort(MONGODB_PORT))
                ));
    }

    @BeforeEach
    void setUp() {
        productRepository.deleteAll();
        inventoryRepository.deleteAll();
    }

    @Test
    void saveAll_ShouldPersistProducts() {
        // Given
        ProductDTO productDTO = new ProductDTO("Dining Chair", 20.0, List.of(
                new ProductArticleDTO("1", "4"),
                new ProductArticleDTO("2", "8")
        ));

        // When
        productService.saveAll(List.of(productDTO));

        // Then
        List<Product> products = productRepository.findAll();
        assertEquals(1, products.size());
        assertEquals("Dining Chair", products.get(0).getName());
        assertEquals(20, products.get(0).getPrice());
    }

    @Test
    void findAll_ShouldReturnOnlyAvailableProducts() {
        // Given
        Product product = new Product();
        product.setName("Table");
        product.setPrice(50.0);
        product.setContainArticles(List.of(
                new Product.ArticleQuantity("1", 5L),
                new Product.ArticleQuantity("2", 3L)
        ));
        productRepository.save(product);

        inventoryRepository.save(new Inventory("1", "Wood", 10L));
        inventoryRepository.save(new Inventory("2", "Screws", 2L)); // Not enough stock

        // When
        List<Product> availableProducts = productService.findAll();

        // Then
        assertEquals(0, availableProducts.size()); // Should be empty due to insufficient stock
    }

    @Test
    void sell_ShouldReduceInventory() {
        // Given
        Product product = new Product();
        product.setId("123");
        product.setName("Table");
        product.setPrice(50.0);
        product.setContainArticles(List.of(
                new Product.ArticleQuantity("1", 5L),
                new Product.ArticleQuantity("2", 3L)
        ));
        productRepository.save(product);

        inventoryRepository.save(new Inventory("1", "Wood", 10L));
        inventoryRepository.save(new Inventory("2", "Screws", 5L));

        // When
        productService.sell("123");

        // Then
        Inventory inventory1 = inventoryRepository.findById("1").orElseThrow();
        Inventory inventory2 = inventoryRepository.findById("2").orElseThrow();

        assertEquals(5, inventory1.getStock()); // 10 - 5 = 5
        assertEquals(2, inventory2.getStock()); // 5 - 3 = 2
    }

    @Test
    void sell_ShouldThrowProductNotFoundException() {
        assertThrows(ProductNotFoundException.class, () -> productService.sell("nonexistent-id"));
    }

    @Test
    void sell_ShouldThrowProductSoldOutException() {
        // Given
        Product product = new Product();
        product.setId("123");
        product.setName("Table");
        product.setPrice(50.0);
        product.setContainArticles(List.of(
                new Product.ArticleQuantity("1", 5L),
                new Product.ArticleQuantity("2", 3L)
        ));
        productRepository.save(product);

        // Inventory has missing stock
        inventoryRepository.save(new Inventory("1", "Wood", 2L)); // Not enough stock
        inventoryRepository.save(new Inventory("2", "Screws", 5L));

        // Then
        assertThrows(ProductSoldOutException.class, () -> productService.sell("123"));
    }
}
