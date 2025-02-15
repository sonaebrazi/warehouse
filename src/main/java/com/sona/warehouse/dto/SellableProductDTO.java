package com.sona.warehouse.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * Data Transfer Object (DTO) that represents a sellable product with its details,
 * including the product's ID, name, price, the list of contained articles with required quantities,
 * and the quantity of products that can be sold based on the available inventory.
 */
@Data
@AllArgsConstructor
@Builder
public class SellableProductDTO {

    private String id;
    private String name;
    private Double price;
    private List<ProductArticleDTO> containArticles;
    private Long quantity;

}
