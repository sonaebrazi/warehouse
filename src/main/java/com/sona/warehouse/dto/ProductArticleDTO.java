package com.sona.warehouse.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProductArticleDTO {
    @JsonProperty("art_id")
    private String articleId;
    @JsonProperty("amount_of")
    private String amountOf;
}
