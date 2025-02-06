package com.sona.warehouse.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * A custom exception that holds an HTTP status code and a message.
 * This exception is used to indicate specific error conditions that
 * correspond to HTTP response statuses.
 */
@Getter
public class CustomHttpStatusCodeException extends RuntimeException {
    private final HttpStatus httpStatus;
    private final String message;

    /**
     * Constructs a new CustomHttpStatusCodeException with the specified HTTP status and detail message.
     *
     * @param httpStatus the HTTP status code associated with this exception
     * @param message    the detail message for this exception
     */
    public CustomHttpStatusCodeException(HttpStatus httpStatus, String message) {
        super(message);
        this.httpStatus = httpStatus;
        this.message = message;
    }
}
