package com.sona.warehouse.repository;

import com.sona.warehouse.model.Product;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@DataMongoTest
@ExtendWith(SpringExtension.class)
class ProductRepositoryTest extends BaseRepositoryTest {

    @Autowired
    ProductRepository productRepository;

    @Override
    protected void clearDatabase() {
        productRepository.deleteAll();
    }

    @Test
    void saveAndFindProduct() {
        // Given a product
        Product product = createProduct("Dining Chair", 20);

        // When saving the product
        productRepository.save(product);

        // Then it should be retrievable
        verifyProductRetrieval(product);
    }

    private Product createProduct(String name, double price) {
        return Product.builder()
                .name(name)
                .price(price)
                .build();
    }

    private void verifyProductRetrieval(Product expectedProduct) {
        List<Product> products = productRepository.findAll();
        assertEquals(1, products.size());
        assertEquals(expectedProduct.getName(), products.get(0).getName());
        assertEquals(expectedProduct.getPrice(), products.get(0).getPrice());
        assertNotNull(products.get(0).getId());
    }

    @Test
    void updateProduct() {
        // Given a product
        Product product = createProduct("Dining Table", 100);
        product = productRepository.save(product);

        // When updating the product's price
        product.setPrice(120.0);
        productRepository.save(product);

        // Then it should be updated in the repository
        Product updatedProduct = productRepository.findById(product.getId()).orElse(null);
        assertNotNull(updatedProduct);
        assertEquals(120, updatedProduct.getPrice());
    }

    @Test
    void deleteProduct() {
        // Given a product
        Product product = createProduct("Coffee Table", 150);
        product = productRepository.save(product);

        // When deleting the product
        productRepository.deleteById(product.getId());

        // Then it should not be present anymore
        assertEquals(0, productRepository.count());
    }

    @Test
    void findAllProducts() {
        // Given two products
        Product product1 = createProduct("Dining Chair", 20);
        Product product2 = createProduct("Sofa", 300);
        productRepository.save(product1);
        productRepository.save(product2);

        // When retrieving all products
        List<Product> products = productRepository.findAll();

        // Then it should return both products
        assertEquals(2, products.size());
        assertTrue(products.contains(product1));
        assertTrue(products.contains(product2));
    }

    @Test
    void findNonExistentProduct() {
        // When trying to find a non-existent product
        Product foundProduct = productRepository.findById("non-existent-id").orElse(null);

        // Then it should return null
        assertNull(foundProduct);
    }

    @Test
    void saveProductWithArticles() {
        // Given a product with associated articles
        Product product = createProductWithArticles("Dining Chair", 20);

        // When saving the product
        productRepository.save(product);

        // Then it should be retrievable
        List<Product> products = productRepository.findAll();
        assertEquals(1, products.size());
        assertEquals("Dining Chair", products.get(0).getName());
        assertEquals(20, products.get(0).getPrice());
        assertEquals(3, products.get(0).getContainArticles().size()); // Assuming 3 articles
    }

    private Product createProductWithArticles(String name, double price) {
        return Product.builder()
                .name(name)
                .price(price)
                .containArticles(Arrays.asList(
                        new Product.ArticleQuantity("1", 4L),
                        new Product.ArticleQuantity("2", 8L),
                        new Product.ArticleQuantity("3", 1L)
                ))
                .build();
    }
}
