package com.sona.warehouse.dto;

import lombok.Data;
import java.util.List;

/**
 * Data Transfer Object representing a collection of products to be uploaded.
 */
@Data
public class ProductUploadDTO {

    /**
     * A list of products to be uploaded.
     */
    private List<ProductDTO> products;
}
