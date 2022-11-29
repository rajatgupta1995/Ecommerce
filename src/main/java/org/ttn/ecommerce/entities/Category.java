package org.ttn.ecommerce.entities;

import java.util.Set;
import javax.persistence.*;

import lombok.Data;
@Entity
@Data
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

//    @OneToMany(cascade = CascadeType.ALL, mappedBy = "category")
//    private Set<Product> product;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "parent_category_id")
    private Category category;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "category")
    private Set<CategoryMetadataFieldValue> categoryMetadataField;

}