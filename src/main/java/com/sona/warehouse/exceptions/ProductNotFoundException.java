package com.sona.warehouse.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpStatusCodeException;

public class ProductNotFoundException extends HttpStatusCodeException {
    public ProductNotFoundException(Long id) {
        super(HttpStatus.NOT_FOUND, "Product not found:" + id);
    }
}
