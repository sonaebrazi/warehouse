package com.sona.warehouse.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class CustomHttpStatusCodeException extends RuntimeException{
    HttpStatus httpStatus;
    String message;

    CustomHttpStatusCodeException(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }
}
