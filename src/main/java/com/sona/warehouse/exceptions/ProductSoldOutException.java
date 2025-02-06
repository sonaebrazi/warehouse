package com.sona.warehouse.exceptions;

import org.springframework.http.HttpStatus;

/**
 * Exception thrown when a product is sold out.
 * This exception indicates that the requested product is no longer available
 * for sale due to lack of stock.
 */
public class ProductSoldOutException extends CustomHttpStatusCodeException {

    /**
     * Constructs a new ProductSoldOutException with the specified product ID.
     *
     * @param id the ID of the product that is sold out
     */
    public ProductSoldOutException(String id) {
        super(HttpStatus.CONFLICT, "Product is sold out: " + id);
    }
}
