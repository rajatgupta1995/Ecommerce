package org.ttn.ecommerce.entities.category;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;
import lombok.Data;

@Entity
@Data
public class CategoryMetaDataField {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @OneToMany(mappedBy = "categoryMetaDataField")
    private Set<CategoryMetadataFieldValue> categoryMetadataFieldValues = new HashSet<>();
}
