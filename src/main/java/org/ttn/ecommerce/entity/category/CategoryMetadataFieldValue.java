package org.ttn.ecommerce.entity.category;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

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