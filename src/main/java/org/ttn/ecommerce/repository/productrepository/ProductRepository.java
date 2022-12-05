package org.ttn.ecommerce.repository.productrepository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.ttn.ecommerce.entity.category.Category;
import org.ttn.ecommerce.entity.product.Product;
import org.ttn.ecommerce.entity.register.Seller;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    @Query(value = "SELECT * FROM product WHERE name=:name and category_id=:catId and seller_user_id=:sellerId and brand=:brand" ,nativeQuery = true)
    Optional<Product> findExistingProduct(String name, Long sellerId, String brand, Long catId);
    List<Product> findBySeller(Seller seller);

    List<Product> findByCategory(Category category);

    @Query(value="select * from product where category_id = :id",nativeQuery = true)
    long existsByCategoryId(@Param("id") Long id);

    @Modifying
    @Query(value = "update product set is_deleted = 1 where id = :id",nativeQuery = true)
    void deleteById(@Param("id") Integer id);

}
