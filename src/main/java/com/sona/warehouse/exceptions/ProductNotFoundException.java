package com.sona.warehouse.exceptions;

import org.springframework.http.HttpStatus;

public class ProductNotFoundException extends CustomHttpStatusCodeException {
    public ProductNotFoundException(String id) {
        super(HttpStatus.NOT_FOUND, "Product not found:" + id);
    }
}
