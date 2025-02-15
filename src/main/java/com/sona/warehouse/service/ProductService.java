package com.sona.warehouse.service;

import com.sona.warehouse.dto.ProductArticleDTO;
import com.sona.warehouse.dto.ProductDTO;
import com.sona.warehouse.dto.SellableProductDTO;
import com.sona.warehouse.exceptions.ArticleNotFoundException;
import com.sona.warehouse.exceptions.CustomNumberFormatException;
import com.sona.warehouse.exceptions.ProductNotFoundException;
import com.sona.warehouse.exceptions.ProductSoldOutException;
import com.sona.warehouse.model.Inventory;
import com.sona.warehouse.model.Product;
import com.sona.warehouse.repository.InventoryRepository;
import com.sona.warehouse.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service class responsible for managing product operations.
 * This class handles the business logic related to products,
 * including saving, retrieving, and selling products.
 */
@Service
public class ProductService {
    private static final Logger logger = LoggerFactory.getLogger(ProductService.class);

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
     * @throws CustomNumberFormatException if the amountOf field is not a valid number.
     */
    @Transactional
    public void saveAll(List<ProductDTO> productDTOs) throws CustomNumberFormatException {
        logger.info("Saving {} products", productDTOs.size());
        for (ProductDTO productDTO : productDTOs) {
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
     * @throws CustomNumberFormatException if the amountOf field is not a valid number.
     */
    private Product.ArticleQuantity toModel(ProductArticleDTO articleDTO) throws CustomNumberFormatException {
        try {
            long amountOf = Long.parseLong(articleDTO.getAmountOf());
            return Product.ArticleQuantity.builder()
                    .articleId(articleDTO.getArticleId())
                    .quantity(amountOf)
                    .build();
        } catch (NumberFormatException e) {
            throw new CustomNumberFormatException(articleDTO.getAmountOf());
        }
    }

    /**
     * Retrieves all products and filters them based on their inventory availability.
     *
     * @return a list of available products.
     */
    public List<SellableProductDTO> findAll() {
        logger.info("Fetching all available products.");
        List<Product> allProducts = productRepository.findAll();

        return allProducts.stream()
                .map(product -> {
                    long quantity = findQuantity(product);

                    if (quantity > 0) {
                        return SellableProductDTO.builder()
                                .id(product.getId())
                                .name(product.getName())
                                .price(product.getPrice())
                                .containArticles(toDto(product.getContainArticles()))
                                .quantity(quantity)
                                .build();
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private List<ProductArticleDTO> toDto(List<Product.ArticleQuantity> containArticles) {
        return containArticles.stream().map(conArt ->
                        ProductArticleDTO.builder()
                                .articleId(conArt.getArticleId())
                                .amountOf(Long.toString(conArt.getQuantity()))
                                .build())
                .collect(Collectors.toList());
    }

    /**
     * Sells a product with the specified ID.
     * This method checks if the product exists and if it is in stock,
     * then deducts the required articles from the inventory.
     *
     * @param id the ID of the product to be sold.
     * @throws ProductNotFoundException if the product with the specified ID does not exist.
     * @throws ProductSoldOutException  if the product is sold out.
     * @throws ArticleNotFoundException if an article required for the product is not found.
     */
    @Transactional
    public void sell(String id) {
        logger.info("Processing sale for product ID: {}", id);
        Optional<Product> productOpt = productRepository.findById(id);

        if (productOpt.isEmpty()) {
            logger.warn("Product with ID {} not found!", id);
            throw new ProductNotFoundException(id);
        }

        Product product = productOpt.get();

        if (findQuantity(product) <= 0) {
            logger.warn("Product with ID {} is sold out!", id);
            throw new ProductSoldOutException(id);
        }

        // Deduct required articles from inventory
        product.getContainArticles().forEach(articleQuantity -> {
            Inventory inventory = inventoryRepository.findById(articleQuantity.getArticleId())
                    .orElseThrow(() -> new ArticleNotFoundException(articleQuantity.getArticleId()));

            inventory.setStock(inventory.getStock() - articleQuantity.getQuantity());
            inventoryRepository.save(inventory);
            logger.debug("Reduced stock for article ID {}: new stock {}", articleQuantity.getArticleId(), inventory.getStock());
        });
    }

    /**
     * Determines how many units of a product can be made based on the available stock of the required articles.
     * The method calculates the maximum number of products that can be produced with the current inventory,
     * considering the quantities of each article required to produce one unit of the product.
     *
     * @param product the product whose sellable quantity is being calculated.
     * @return the maximum number of units of the product that can be made based on the available stock of the articles.
     *         Returns 0 if any required article is missing from the inventory.
     */
    private Long findQuantity(Product product) {
        List<Product.ArticleQuantity> neededArticles = product.getContainArticles();

        long minAvailableProducts = Long.MAX_VALUE;
        for (Product.ArticleQuantity neededArticle : neededArticles) {
            long availableProducts;
            Optional<Inventory> articleInInventory = inventoryRepository.findById(neededArticle.getArticleId());
            if (articleInInventory.isEmpty()) {
                return 0L;
            }
            availableProducts = articleInInventory.get().getStock() / neededArticle.getQuantity();
            if (availableProducts < minAvailableProducts) {
                minAvailableProducts = availableProducts;
            }
        }
        return minAvailableProducts;
    }
}
