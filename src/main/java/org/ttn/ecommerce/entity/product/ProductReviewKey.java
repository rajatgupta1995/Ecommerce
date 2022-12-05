package org.ttn.ecommerce.entity.product;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class ProductReviewKey implements Serializable {
    @Column(name = "CUSTOMER_ID")
    long customerId;

    @Column(name = "PRODUCT_ID")
    long productId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProductReviewKey that = (ProductReviewKey) o;
        return customerId == that.customerId && productId == that.productId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(customerId, productId);
    }
}
