package com.sona.warehouse.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

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
