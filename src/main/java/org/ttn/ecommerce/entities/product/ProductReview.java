package org.ttn.ecommerce.entities.product;

import lombok.Data;
import org.ttn.ecommerce.entities.register.Customer;

import javax.persistence.*;

@Entity
@Data
public class ProductReview {
    @EmbeddedId
    private ProductReviewKey id;

    @ManyToOne
    @MapsId("productId")
    @JoinColumn(name = "PRODUCT_ID")
    private Product product;

    @ManyToOne
    @MapsId("customerId")
    @JoinColumn(name = "CUSTOMER_USER_ID")
    private Customer customer;

    private String review;

    private int rating;
}
