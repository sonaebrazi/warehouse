package com.sona.warehouse.exceptions;

import org.springframework.http.HttpStatus;

public class ProductNotFoundException extends AbstractCustomHttpStatusCodeException {
    public ProductNotFoundException(Long id) {
        super(HttpStatus.NOT_FOUND, "Product not found:" + id);
    }
}
