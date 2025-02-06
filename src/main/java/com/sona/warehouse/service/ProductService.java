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

/**
 * Service class responsible for managing product operations.
 * This class handles the business logic related to products,
 * including saving, retrieving, and selling products.
 */
@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final InventoryRepository inventoryRepository;

    /**
     * Constructs a ProductService with the specified ProductRepository
     * and InventoryRepository.
     *
     * @param productRepository   the repository for accessing product data.
     * @param inventoryRepository the repository for accessing inventory data.
     */
    @Autowired
    public ProductService(ProductRepository productRepository, InventoryRepository inventoryRepository) {
        this.productRepository = productRepository;
        this.inventoryRepository = inventoryRepository;
    }

    /**
     * Saves all products provided in the list of ProductDTOs.
     * This method updates existing products or creates new ones
     * based on the provided product data.
     *
     * @param productDTOs the list of ProductDTOs to be saved.
     */
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

    /**
     * Converts a ProductDTO to a Product model.
     *
     * @param productDTO the ProductDTO to be converted.
     * @return the corresponding Product model.
     */
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

    /**
     * Converts a ProductArticleDTO to a Product.ArticleQuantity model.
     *
     * @param articleDTO the ProductArticleDTO to be converted.
     * @return the corresponding Product.ArticleQuantity model.
     */
    private Product.ArticleQuantity toModel(ProductArticleDTO articleDTO) {
        return Product.ArticleQuantity.builder()
                .articleId(articleDTO.getArticleId())
                .quantity(Long.parseLong(articleDTO.getAmountOf()))
                .build();
    }

    /**
     * Retrieves all products and filters them based on their inventory availability.
     *
     * @return a list of available products.
     */
    public List<Product> findAll() {
        List<Product> allProducts = productRepository.findAll();

        return allProducts.stream()
                .filter(this::checkInventory)
                .collect(Collectors.toList());
    }

    /**
     * Sells a product with the specified ID.
     * This method checks if the product exists and if it is in stock,
     * then deducts the required articles from the inventory.
     *
     * @param id the ID of the product to be sold.
     * @throws ProductNotFoundException    if the product with the specified ID does not exist.
     * @throws ProductSoldOutException      if the product is sold out.
     * @throws ArticleNotFoundException     if an article required for the product is not found.
     */
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

    /**
     * Checks the inventory availability for the specified product.
     *
     * @param product the product to check.
     * @return true if all required articles are available in sufficient quantity; false otherwise.
     */
    private boolean checkInventory(Product product) {
        return product.getContainArticles().stream()
                .allMatch(articleQuantity -> {
                    String articleId = articleQuantity.getArticleId();
                    Long quantityNeeded = articleQuantity.getQuantity();
                    return inventoryRepository.findById(articleId)
                            .map(inventory -> inventory.getStock() >= quantityNeeded)
                            .orElse(false);
                });
    }
}
