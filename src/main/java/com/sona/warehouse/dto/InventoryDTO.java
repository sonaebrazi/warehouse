package com.sona.warehouse.dto;

import lombok.Data;
import java.util.List;

/**
 * Data Transfer Object representing a collection of inventory articles.
 */
@Data
public class InventoryDTO {

    /**
     * A list of inventory articles.
     */
    private List<InventoryArticleDTO> inventory;
}
