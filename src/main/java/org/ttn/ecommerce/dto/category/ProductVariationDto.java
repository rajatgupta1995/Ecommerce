package org.ttn.ecommerce.dto.category;

import lombok.Data;

@Data
public class ProductVariationDto {

    private Long productId;
    private String metaData;
    private Integer quantity;
    private Float price;


}
