package com.sona.warehouse.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Data Transfer Object representing an article in the inventory.
 */
@Data
public class InventoryArticleDTO {

    /**
     * The unique identifier for the article.
     */
    @JsonProperty("art_id")
    private String articleId;

    /**
     * The name of the article.
     */
    private String name;

    /**
     * The stock quantity of the article.
     * This value is represented as a String to accommodate any potential formatting requirements.
     */
    private String stock;
}
