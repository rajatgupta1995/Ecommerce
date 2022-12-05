package org.ttn.ecommerce.entity.product;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.ttn.ecommerce.entity.category.Category;
import org.ttn.ecommerce.entity.register.Seller;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Entity
@Data
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @NotNull
    @NotEmpty
    private String name;
    private String description;

    @JsonProperty
    @Column(name = "Is_Cancellable")
    private boolean isCancellable;

    @JsonProperty
    @Column(name = "Is_Returnable")
    private boolean isReturnable;

    @NotNull
    @NotEmpty
    private String brand;

    @JsonProperty
    @Column(name = "Is_Active")
    private boolean isActive;

    @JsonProperty
    @Column(name = "Is_Deleted")
    private boolean isDeleted;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "Seller_User_Id")
    private Seller seller;

    @ManyToOne
    @JoinColumn(name = "Category_Id")
    private Category category;

    @JsonIgnore
    @OneToMany(mappedBy = "product")
    private List<ProductVariation> productVariations;

    @JsonIgnore
    @OneToMany(mappedBy = "product")
    private List<ProductReview> productReviews;
}
