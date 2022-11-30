package org.ttn.ecommerce.entities.category;

import lombok.Data;
import lombok.EqualsAndHashCode;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Data
@Embeddable
@EqualsAndHashCode
public class CategoryCompositeKey implements Serializable {

    @Column(name="category_id")
    private Long categoryId;

    @Column(name="category_meta_data_field_id")
    private Long categoryMetaDataFieldId;
}
