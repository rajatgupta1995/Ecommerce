package org.ttn.ecommerce.entities.category;

import javax.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.ttn.ecommerce.entities.category.Category;
import org.ttn.ecommerce.entities.category.CategoryCompositeKey;
import org.ttn.ecommerce.entities.category.CategoryMetaDataField;

@Getter
@Setter
@Entity
public class CategoryMetadataFieldValue {
    @EmbeddedId
    private CategoryCompositeKey id=new CategoryCompositeKey();

    private String value;


    //@JoinColumn(name = "category_id")
    @ManyToOne(fetch = FetchType.EAGER)
    @MapsId("categoryId")
    private Category category;


    //@JoinColumn(name = "category_meta_data_field_id")
    @ManyToOne
    @MapsId("categoryMetaDataFieldId")
    private CategoryMetaDataField categoryMetaDataField;
}