package org.ttn.ecommerce.repository.category;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.ttn.ecommerce.entities.category.CategoryMetaDataField;

import java.util.Optional;

@Repository
public interface CategoryMetaDataFieldRepository extends JpaRepository<CategoryMetaDataField, Long> {

    @Query(value="SELECT * FROM category_meta_data_field WHERE name= ?1", nativeQuery = true)
    Optional<CategoryMetaDataField> findByName(String fieldName);
}