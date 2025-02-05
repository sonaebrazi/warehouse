package com.sona.warehouse.exceptions;

import org.springframework.http.HttpStatus;

public class ProductSoldOutException extends AbstractCustomHttpStatusCodeException {
    public ProductSoldOutException(Long id) {
        super(HttpStatus.CONFLICT, "Product is sold out: " + id);
    }
}