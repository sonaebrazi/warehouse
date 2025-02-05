package com.sona.warehouse.exceptions;

import org.springframework.http.HttpStatus;

public class ArticleNotFoundException extends AbstractCustomHttpStatusCodeException {
    public ArticleNotFoundException(String id) {
        super(HttpStatus.NOT_FOUND, "Article not found:" + id);
    }
}
