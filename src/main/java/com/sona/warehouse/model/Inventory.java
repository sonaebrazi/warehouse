package com.sona.warehouse.model;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "inventory")
@Data
@Builder
public class Inventory {

    @Id
    private String id;

    private String name;
    private int stock;
}
