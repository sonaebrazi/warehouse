package com.sona.warehouse.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class InventoryArticleDTO {
    @JsonProperty("art_id")
    private String articleId;
    private String name;
    private String stock;

}
