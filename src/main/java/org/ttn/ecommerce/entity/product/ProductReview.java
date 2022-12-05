package org.ttn.ecommerce.entity.product;

import lombok.Data;
import org.ttn.ecommerce.entity.register.Customer;

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
