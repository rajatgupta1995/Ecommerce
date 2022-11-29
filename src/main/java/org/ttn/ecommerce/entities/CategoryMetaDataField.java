package org.ttn.ecommerce.entities;

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

    @OneToMany(mappedBy = "CategoryMetaDataField",cascade = CascadeType.ALL)
    private Set<CategoryMetadataFieldValue> categoryMetadataFieldValues = new HashSet<>();

//    @ManyToOne(cascade = CascadeType.ALL)
//    @JoinColumn(name = "category_id", nullable = false)
//    private Category category;

}
