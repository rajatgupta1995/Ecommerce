package org.ttn.ecommerce.dto.category;


import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class CategoryDto {
    @NotNull
    private String name;
    private Long parentCategoryId;
}