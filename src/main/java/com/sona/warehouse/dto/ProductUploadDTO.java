package com.sona.warehouse.dto;

import lombok.Data;
import java.util.List;

@Data
public class ProductUploadDTO {
    private List<ProductDTO> products;
}
