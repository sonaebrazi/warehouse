package com.sona.warehouse.exceptions;

import org.springframework.http.HttpStatus;

public class ProductSoldOutException extends CustomHttpStatusCodeException {
    public ProductSoldOutException(String id) {
        super(HttpStatus.CONFLICT, "Product is sold out: " + id);
    }
}