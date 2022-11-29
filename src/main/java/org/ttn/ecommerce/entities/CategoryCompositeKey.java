package org.ttn.ecommerce.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;

@Data
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class CategoryCompositeKey implements Serializable {
    @Column(name="category_id")
    private Long categoryId;

    @Column(name="category_meta_data_field_id")
    private Long categoryMetaDataFieldId;
}
