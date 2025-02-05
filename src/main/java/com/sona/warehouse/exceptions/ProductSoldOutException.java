package com.sona.warehouse.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpStatusCodeException;

public class ProductSoldOutException extends HttpStatusCodeException {
    protected ProductSoldOutException(Long id) {
        super(HttpStatus.CONFLICT, "Product is sold out: " + id);
    }
}