package com.sona.warehouse.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "products")
@Data
@Builder
public class Product {

    @Id
    private String id;

    private String name;
    private double price;

    private List<ArticleQuantity> containArticles;

    public static class ArticleQuantity {
        String articleId;
        int quantity;

        public ArticleQuantity(String articleId, int quantity) {
            this.articleId = articleId;
            this.quantity = quantity;
        }
    }
}
