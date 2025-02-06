package com.sona.warehouse.exceptions;

import org.springframework.http.HttpStatus;

/**
 * Exception thrown when a product cannot be found.
 * This exception is used to indicate that a requested product
 * does not exist in the inventory or database.
 */
public class ProductNotFoundException extends CustomHttpStatusCodeException {

    /**
     * Constructs a new ProductNotFoundException with the specified product ID.
     *
     * @param id the ID of the product that was not found
     */
    public ProductNotFoundException(String id) {
        super(HttpStatus.NOT_FOUND, "Product not found: " + id);
    }
}
