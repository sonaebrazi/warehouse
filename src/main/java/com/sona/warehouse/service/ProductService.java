package com.sona.warehouse.service;

import com.sona.warehouse.dto.ProductArticleDTO;
import com.sona.warehouse.dto.ProductDTO;
import com.sona.warehouse.exceptions.ArticleNotFoundException;
import com.sona.warehouse.exceptions.ProductNotFoundException;
import com.sona.warehouse.exceptions.ProductSoldOutException;
import com.sona.warehouse.model.Inventory;
import com.sona.warehouse.model.Product;
import com.sona.warehouse.repository.InventoryRepository;
import com.sona.warehouse.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final InventoryRepository inventoryRepository;

    @Autowired
    public ProductService(ProductRepository productRepository, InventoryRepository inventoryRepository) {
        this.productRepository = productRepository;
        this.inventoryRepository = inventoryRepository;
    }

    public void saveAll(List<ProductDTO> productDTOs) {
        for (ProductDTO productDTO : productDTOs) {
            System.out.println(productDTO);
            Optional<Product> existingProduct = productRepository.findByName(productDTO.getName());

            if (existingProduct.isPresent()) {
                Product existing = existingProduct.get();
                existing.setPrice(productDTO.getPrice());
                existing.setContainArticles(
                        productDTO.getContainArticles().stream()
                                .map(this::toModel)
                                .collect(Collectors.toList())
                );
                productRepository.save(existing);
            } else {
                productRepository.save(toModel(productDTO));
            }
        }
    }

    private Product toModel(ProductDTO productDTO) {
        return Product.builder()
                .name(productDTO.getName())
                .price(productDTO.getPrice())
                .containArticles(
                        productDTO.getContainArticles().stream()
                                .map(this::toModel)
                                .collect(Collectors.toList())
                )
                .build();
    }

    private Product.ArticleQuantity toModel(ProductArticleDTO articleDTO) {
        return Product.ArticleQuantity.builder()
                .articleId(articleDTO.getArticleId())
                .quantity(Integer.parseInt(articleDTO.getAmountOf()))
                .build();
    }

    public List<Product> findAll() {
        List<Product> allProducts = productRepository.findAll();

        return allProducts.stream()
                .filter(this::checkInventory)
                .collect(Collectors.toList());
    }

    @Transactional
    public void sell(String id) {
        Optional<Product> productOpt = productRepository.findById(id);

        if (productOpt.isEmpty()) {
            throw new ProductNotFoundException(id);
        }

        Product product = productOpt.get();

        if (!checkInventory(product)) {
            throw new ProductSoldOutException(id);
        }

        // Deduct required articles from inventory
        product.getContainArticles().forEach(articleQuantity -> {
            Inventory inventory = inventoryRepository.findById(articleQuantity.getArticleId())
                    .orElseThrow(() -> new ArticleNotFoundException(articleQuantity.getArticleId()));

            inventory.setStock(inventory.getStock() - articleQuantity.getQuantity());
            inventoryRepository.save(inventory);
        });
    }

    private boolean checkInventory(Product product) {
        return product.getContainArticles().stream()
                .allMatch(articleQuantity -> {
                    String articleId = articleQuantity.getArticleId();
                    int quantityNeeded = articleQuantity.getQuantity();
                    return inventoryRepository.findById(articleId)
                            .map(inventory -> inventory.getStock() >= quantityNeeded)
                            .orElse(false);
                });
    }
}
