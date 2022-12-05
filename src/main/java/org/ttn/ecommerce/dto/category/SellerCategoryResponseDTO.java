package org.ttn.ecommerce.dto.category;

import lombok.Getter;
import lombok.Setter;
import org.ttn.ecommerce.entity.category.Category;

import java.util.List;

@Getter
@Setter
public class SellerCategoryResponseDTO {
    private Long id;
    private String name;
    private CategoryDto parent;

    private List<MetadataResponseDTO> metadata;
}
