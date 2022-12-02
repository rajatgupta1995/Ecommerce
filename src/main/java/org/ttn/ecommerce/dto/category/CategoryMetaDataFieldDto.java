package org.ttn.ecommerce.dto.category;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Id;

@Getter
@Setter
public class CategoryMetaDataFieldDto {
    private long id;
    private String name;
}
