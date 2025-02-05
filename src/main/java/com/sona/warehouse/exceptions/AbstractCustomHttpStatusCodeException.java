package com.sona.warehouse.exceptions;

import org.springframework.http.HttpStatus;

abstract class AbstractCustomHttpStatusCodeException extends RuntimeException{
    HttpStatus httpStatus;
    String message;

    AbstractCustomHttpStatusCodeException(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }
}
