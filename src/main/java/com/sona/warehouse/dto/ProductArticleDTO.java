package com.sona.warehouse.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object representing an article associated with a product.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
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
