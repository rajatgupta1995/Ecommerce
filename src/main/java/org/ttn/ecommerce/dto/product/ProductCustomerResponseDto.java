package org.ttn.ecommerce.dto.product;

import lombok.Data;
import org.ttn.ecommerce.dto.category.CategoryDto;
import org.ttn.ecommerce.entity.category.Category;

import java.util.List;

@Data
public class ProductCustomerResponseDto {
    private Long id;

    private String name;

    private String description;

    private String brand;

    private Boolean isActive;

    private Boolean isCancellable;

    private Boolean isReturnable;

    private CategoryDto category;

    private List<VariationResponseDTO> variations;
}
