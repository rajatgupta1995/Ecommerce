package org.ttn.ecommerce.entity.category;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

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
