package org.ttn.ecommerce.dto.category;

import lombok.Data;
import org.ttn.ecommerce.entity.category.Category;

import java.util.HashSet;
import java.util.Set;

@Data
public class CategoryViewDto {
    private long id;
    private String name;
    private Category parent;
    private Set<SubCategoryDto> children = new HashSet<>();
}