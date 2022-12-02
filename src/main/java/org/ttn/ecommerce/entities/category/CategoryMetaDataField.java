package org.ttn.ecommerce.entities.category;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter

@Getter
public class CategoryMetaDataField {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @OneToMany(mappedBy = "categoryMetaDataField",fetch = FetchType.EAGER,cascade = CascadeType.MERGE)
    private Set<CategoryMetadataFieldValue> categoryMetadataFieldValues = new HashSet<>();
}
