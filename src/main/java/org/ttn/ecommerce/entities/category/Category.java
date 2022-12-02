package org.ttn.ecommerce.entities.category;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;

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

//    @OneToMany(cascade = CascadeType.ALL, mappedBy = "category")
//    private Set<Product> product;

    @JsonIgnore
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "parent_category_id")
    private Category parentCategory;

    @JsonIgnore
    @OneToMany(mappedBy = "parentCategory",cascade = CascadeType.ALL,fetch = FetchType.EAGER)
    private List<Category> subCategory;

    @JsonIgnore
    @OneToMany( mappedBy = "category",cascade = CascadeType.MERGE,fetch = FetchType.EAGER)
    private Set<CategoryMetadataFieldValue> categoryMetadataField=new HashSet<>();

}