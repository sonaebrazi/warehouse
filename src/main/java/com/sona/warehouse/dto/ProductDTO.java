package com.sona.warehouse.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Data Transfer Object representing a product with its details.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductDTO {

    /**
     * The name of the product.
     */
    private String name;

    /**
     * The price of the product.
     */
    private Double price;

    /**
     * A list of articles contained in the product.
     * Each article includes its identifier and the amount required.
     */
    @JsonProperty("contain_articles")
    private List<ProductArticleDTO> containArticles;
}
