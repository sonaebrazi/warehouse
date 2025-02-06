package com.sona.warehouse.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Data Transfer Object representing an article associated with a product.
 */
@Data
@AllArgsConstructor
public class ProductArticleDTO {

    /**
     * The unique identifier of the article.
     */
    @JsonProperty("art_id")
    private String articleId;

    /**
     * The amount of the article required for the product.
     */
    @JsonProperty("amount_of")
    private String amountOf;
}
