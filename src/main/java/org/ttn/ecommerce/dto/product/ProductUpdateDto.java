package org.ttn.ecommerce.dto.product;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class ProductUpdateDto {
    private String description;
    private Boolean isCancellable;
    private Boolean isReturnable;
    private String name;
}