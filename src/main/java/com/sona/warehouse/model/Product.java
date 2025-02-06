package com.sona.warehouse.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

/**
 * Represents a product in the warehouse.
 */
@Document(collection = "products")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Product {

    @Id
    private String id;

    private String name;
    private Double price;

    private List<ArticleQuantity> containArticles;

    /**
     * Represents the quantity of an article in the product.
     */
    @Setter
    @Getter
    @Builder
    public static class ArticleQuantity {
        String articleId;
        Long quantity;

        public ArticleQuantity(String articleId, Long quantity) {
            this.articleId = articleId;
            this.quantity = quantity;
        }
    }
}
