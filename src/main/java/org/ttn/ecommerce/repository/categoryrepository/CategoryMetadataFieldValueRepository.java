package org.ttn.ecommerce.repository.categoryrepository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.ttn.ecommerce.entity.category.Category;
import org.ttn.ecommerce.entity.category.CategoryMetadataFieldValue;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryMetadataFieldValueRepository extends JpaRepository<CategoryMetadataFieldValue, Long> {

    @Query(value = "SELECT * FROM category_metadata_field_value a WHERE a.category_meta_data_field_id = ?1", nativeQuery = true)
    Optional<CategoryMetadataFieldValue > findByCategoryMetadataFieldId(Long id);

    List<CategoryMetadataFieldValue> findByCategory(Category category);

    List<CategoryMetadataFieldValue> findByCategoryId(Long id);
}