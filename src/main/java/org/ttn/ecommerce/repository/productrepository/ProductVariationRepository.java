package org.ttn.ecommerce.repository.productrepository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.ttn.ecommerce.entity.product.Product;
import org.ttn.ecommerce.entity.product.ProductVariation;

import java.util.List;

@Repository
public interface ProductVariationRepository extends JpaRepository<ProductVariation,Long> {
    List<ProductVariation> findByProduct(Product product);
}
