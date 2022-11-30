package org.ttn.ecommerce.entities.category;

import javax.persistence.*;
import lombok.Data;
import org.ttn.ecommerce.entities.category.Category;
import org.ttn.ecommerce.entities.category.CategoryCompositeKey;
import org.ttn.ecommerce.entities.category.CategoryMetaDataField;

@Data
@Entity
public class CategoryMetadataFieldValue {
    @EmbeddedId
    private CategoryCompositeKey id=new CategoryCompositeKey();

    private String value;

    @ManyToOne
    @MapsId("categoryId")
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne
    @MapsId("categoryMetaDataFieldId")
    @JoinColumn(name = "category_meta_data_field_id")
    private CategoryMetaDataField categoryMetaDataField;
}