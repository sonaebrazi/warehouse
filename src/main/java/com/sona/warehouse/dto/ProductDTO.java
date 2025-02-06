package com.sona.warehouse.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;

@Data
public class ProductDTO {
    private String name;
    @JsonProperty("contain_articles")
    private List<ProductArticleDTO> containArticles;
    private Double price;
}
