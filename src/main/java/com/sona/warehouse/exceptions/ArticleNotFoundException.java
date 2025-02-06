package com.sona.warehouse.exceptions;

import org.springframework.http.HttpStatus;

/**
 * Exception thrown when an article is not found in the inventory.
 */
public class ArticleNotFoundException extends CustomHttpStatusCodeException {

    /**
     * Constructs a new ArticleNotFoundException with the specified detail message.
     *
     * @param id the ID of the article that was not found
     */
    public ArticleNotFoundException(String id) {
        super(HttpStatus.NOT_FOUND, "Article not found: " + id);
    }
}
