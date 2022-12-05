package org.ttn.ecommerce.dto.product;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.ttn.ecommerce.dto.category.CategoryDto;
import org.ttn.ecommerce.entity.category.Category;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductViewDto {
    private Long productId;
    private String name;
    private String description;
    private String brand;

    private boolean isCancellable;

    private boolean isReturnable;
    private boolean isActive;
    private CategoryDto category;
}
