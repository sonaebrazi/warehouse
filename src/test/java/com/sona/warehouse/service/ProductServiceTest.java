package com.sona.warehouse.service;

import com.sona.warehouse.dto.ProductArticleDTO;
import com.sona.warehouse.dto.ProductDTO;
import com.sona.warehouse.exceptions.ProductNotFoundException;
import com.sona.warehouse.exceptions.ProductSoldOutException;
import com.sona.warehouse.model.Inventory;
import com.sona.warehouse.model.Product;
import com.sona.warehouse.repository.InventoryRepository;
import com.sona.warehouse.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private InventoryRepository inventoryRepository;

    @InjectMocks
    private ProductService productService;

    private ProductDTO sampleProductDTO;
    private Product sampleProduct;
    private Inventory sampleInventory;

    @BeforeEach
    void setUp() {
        // Sample Product Article DTO
        ProductArticleDTO articleDTO = new ProductArticleDTO("1", "4");

        // Sample Product DTO
        sampleProductDTO = new ProductDTO();
        sampleProductDTO.setName("Dining Chair");
        sampleProductDTO.setPrice(20.0);
        sampleProductDTO.setContainArticles(List.of(articleDTO));

        // Sample Product Entity
        sampleProduct = Product.builder()
                .id("123")
                .name("Dining Chair")
                .price(20.0)
                .containArticles(sampleProductDTO.getContainArticles().stream()
                        .map(dto -> new Product.ArticleQuantity(dto.getArticleId(), Long.parseLong(dto.getAmountOf())))
                        .collect(Collectors.toList()))
                .build();

        // Sample Inventory
        sampleInventory = new Inventory("1", "Leg", 10L);
    }

    @Test
    void saveAll_ShouldSaveNewProduct() {
        when(productRepository.findByName(sampleProductDTO.getName())).thenReturn(Optional.empty());

        productService.saveAll(List.of(sampleProductDTO));

        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    void saveAll_ShouldUpdateExistingProduct() {
        when(productRepository.findByName(sampleProductDTO.getName())).thenReturn(Optional.of(sampleProduct));

        productService.saveAll(List.of(sampleProductDTO));

        verify(productRepository, times(1)).save(sampleProduct);
        assertEquals(20.0, sampleProduct.getPrice());
    }

    @Test
    void findAll_ShouldReturnAvailableProducts() {
        when(productRepository.findAll()).thenReturn(List.of(sampleProduct));
        when(inventoryRepository.findById("1")).thenReturn(Optional.of(sampleInventory));

        List<Product> products = productService.findAll();

        assertEquals(1, products.size());
        assertEquals("Dining Chair", products.get(0).getName());
    }

    @Test
    void findAll_ShouldFilterOutSoldOutProducts() {
        sampleInventory.setStock(2L); // Not enough stock
        when(productRepository.findAll()).thenReturn(List.of(sampleProduct));
        when(inventoryRepository.findById("1")).thenReturn(Optional.of(sampleInventory));

        List<Product> products = productService.findAll();

        assertEquals(0, products.size());
    }

    @Test
    void sell_ShouldReduceInventory() {
        when(productRepository.findById("123")).thenReturn(Optional.of(sampleProduct));
        when(inventoryRepository.findById("1")).thenReturn(Optional.of(sampleInventory));

        productService.sell("123");

        assertEquals(6, sampleInventory.getStock());
        verify(inventoryRepository, times(1)).save(sampleInventory);
    }

    @Test
    void sell_ShouldThrowProductNotFoundException() {
        when(productRepository.findById("999")).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class, () -> productService.sell("999"));
    }

    @Test
    void sell_ShouldThrowProductSoldOutException() {
        sampleInventory.setStock(2L); // Not enough stock
        when(productRepository.findById("123")).thenReturn(Optional.of(sampleProduct));
        when(inventoryRepository.findById("1")).thenReturn(Optional.of(sampleInventory));

        assertThrows(ProductSoldOutException.class, () -> productService.sell("123"));
    }
}
