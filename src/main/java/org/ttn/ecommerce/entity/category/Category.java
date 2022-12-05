package org.ttn.ecommerce.entity.category;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Getter;
import lombok.Setter;
import org.ttn.ecommerce.entity.product.Product;

import javax.persistence.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Setter
@Getter
//@EqualsAndHashCode.Exclude
//@ToString.Exclude
@Table(name = "category")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "category")
    private Set<Product> product;

    //@JsonManagedReference
    //@JsonIgnore
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "parent_category_id")
    private Category parentCategory;

    //@JsonBackReference
    //@JsonIgnore
    @OneToMany(mappedBy = "parentCategory",cascade = CascadeType.ALL,fetch = FetchType.EAGER)
    private List<Category> subCategory;

    //@JsonIgnore
    @OneToMany( mappedBy = "category",cascade = CascadeType.MERGE,fetch = FetchType.EAGER)
    private Set<CategoryMetadataFieldValue> categoryMetadataField=new HashSet<>();

}