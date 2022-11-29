package org.ttn.ecommerce.entities;

import javax.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
public class CategoryMetadataFieldValue {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String value;

    @ManyToOne
    @MapsId("category_id")
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne(cascade = CascadeType.ALL)
    @MapsId("categoryMetaDataFieldId")
    @JoinColumn(name = "category-metaData_field_id")
    private CategoryMetaDataField categoryMetaDataField;
}