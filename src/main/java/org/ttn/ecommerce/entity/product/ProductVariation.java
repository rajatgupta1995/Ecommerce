package org.ttn.ecommerce.entity.product;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Entity
@Data
public class ProductVariation {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Min(value = 0, message = "Quantity should be greater than 0")
    @NotNull
    private int quantityAvailable;

    @Min(value = 0, message = "Price should be greater than 0")
    @NotNull
    private double price;

    @JsonProperty
    private boolean isActive;

    // how to store json?
    @Column(columnDefinition = "JSON")
    private String metadata;

    //String or byte[] ?
    //private String primaryImageName;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "Product_Id")
    private Product product;
}
