package org.ttn.ecommerce.dto.product;

import lombok.Data;

import javax.persistence.Column;
import javax.validation.constraints.NotNull;

@Data
public class ProductDto {
    @NotNull
    @Column(unique = true)
    private String name;

    @NotNull
    private String brand;

    private String description;

    private Boolean is_returnable;

    private Boolean is_cancellable;
}
