package com.sona.warehouse.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sona.warehouse.dto.ProductUploadDTO;
import com.sona.warehouse.exceptions.CustomHttpStatusCodeException;
import com.sona.warehouse.model.Product;
import com.sona.warehouse.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * REST controller for managing products in the warehouse.
 * Provides endpoints for uploading product data, retrieving products, and selling products.
 */
@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;
    private final ObjectMapper objectMapper;

    /**
     * Constructs a ProductController with the specified ProductService and ObjectMapper.
     *
     * @param productService the service used for product operations
     * @param objectMapper the ObjectMapper used for JSON processing
     */
    @Autowired
    public ProductController(ProductService productService, ObjectMapper objectMapper) {
        this.productService = productService;
        this.objectMapper = objectMapper;
    }

    /**
     * Retrieves a list of all products.
     *
     * @return a ResponseEntity containing the list of products and an OK status
     */
    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        List<Product> products = productService.findAll();
        return ResponseEntity.ok(products);
    }

    /**
     * Sells a product by its ID.
     *
     * @param id the ID of the product to be sold
     * @return a ResponseEntity with a success message or an error message in case of failure
     */
    @PatchMapping("/{id}")
    public ResponseEntity<String> sellProduct(@PathVariable String id) {
        try {
            productService.sell(id);
            return ResponseEntity.ok("Product sold successfully.");
        } catch (CustomHttpStatusCodeException e) {
            return ResponseEntity.status(e.getHttpStatus()).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to sell product: " + e.getMessage());
        }
    }

    /**
     * Uploads products from a JSON file.
     *
     * @param file the MultipartFile containing the JSON data for products
     * @return a ResponseEntity with a success message or an error message in case of failure
     */
    @PostMapping("/upload")
    public ResponseEntity<String> uploadProducts(@RequestParam("file") MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return ResponseEntity.badRequest().body("File is empty or not provided.");
        }

        try {
            // Check if the file content is valid JSON
            byte[] bytes = file.getBytes();

            ProductUploadDTO uploaded = objectMapper.readValue(bytes, objectMapper.getTypeFactory().constructType(ProductUploadDTO.class));
            System.out.println(uploaded);
            // Save all products
            productService.saveAll(uploaded.getProducts());
            return ResponseEntity.ok("Products uploaded successfully!");

        } catch (JsonMappingException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid JSON format: " + e.getMessage());
        } catch (JsonProcessingException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error processing JSON: " + e.getMessage());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload products file: " + e.getMessage());
        }
    }
}
