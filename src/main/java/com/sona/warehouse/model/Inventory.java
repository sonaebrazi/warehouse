package com.sona.warehouse.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Represents an inventory item in the warehouse.
 * This class holds the details about a specific article in stock.
 */
@Document(collection = "inventory")
@Data
@Builder
@AllArgsConstructor
public class Inventory {

    @Id
    private String articleId;
    private String name;
    private Long stock;
}
