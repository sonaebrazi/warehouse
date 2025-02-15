package com.sona.warehouse.exceptions;

import org.springframework.http.HttpStatus;

/**
 * Exception thrown when a number received is not parsable.
 */
public class CustomNumberFormatException extends CustomHttpStatusCodeException {

    /**
     * Constructs a new NumberFormatException
     *
     * @param number the stock of the article or the price of the product
     */
    public CustomNumberFormatException(String number) {
        super(HttpStatus.BAD_REQUEST, "Cannot parse number: " + number);
    }
}
