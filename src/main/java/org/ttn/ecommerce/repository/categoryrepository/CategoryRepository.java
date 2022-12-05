package org.ttn.ecommerce.repository.categoryrepository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.ttn.ecommerce.entity.category.Category;

import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    @Query(value = "SELECT * FROM category WHERE name = ?1", nativeQuery = true)
    Optional<Category> findByCategoryName(String categoryName);
}
