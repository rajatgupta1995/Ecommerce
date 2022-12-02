package org.ttn.ecommerce.repository.categoryRepository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.ttn.ecommerce.entities.category.CategoryMetadataFieldValue;

import java.util.Optional;

@Repository
public interface CategoryMetadataFieldValueRepository extends JpaRepository<CategoryMetadataFieldValue, Long> {

    @Query(value = "SELECT * FROM category_metadata_field_value a WHERE a.category_meta_data_field_id = ?1", nativeQuery = true)
    Optional<CategoryMetadataFieldValue > findByCategoryMetadataFieldId(Long id);
}