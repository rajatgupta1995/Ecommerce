package org.ttn.ecommerce.entities.category;

import java.util.List;
import java.util.Set;
import javax.persistence.*;

import lombok.Data;

@Entity
@Data
@Table(name = "category")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

//    @OneToMany(cascade = CascadeType.ALL, mappedBy = "category")
//    private Set<Product> product;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "parent_category_id")
    private Category parentCategory;

    @OneToMany(mappedBy = "parentCategory")
    private List<Category> subCategory;

    @OneToMany( mappedBy = "category")
    private Set<CategoryMetadataFieldValue> categoryMetadataField;

}