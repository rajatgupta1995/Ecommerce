package org.ttn.ecommerce.dto.product;

import lombok.*;
import org.ttn.ecommerce.entities.category.Category;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductViewDto {
    private Long productId;
    private String name;
    private String description;
    private String brand;
    private boolean isActive;
    private Category category;
}
