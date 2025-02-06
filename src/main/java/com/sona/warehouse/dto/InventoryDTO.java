package com.sona.warehouse.dto;

import lombok.Data;

import java.util.List;

@Data
public class InventoryDTO {
    private List<InventoryArticleDTO> inventory;
}
