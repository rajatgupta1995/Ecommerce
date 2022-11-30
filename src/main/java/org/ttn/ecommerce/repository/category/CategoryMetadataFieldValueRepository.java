package org.ttn.ecommerce.repository.category;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.ttn.ecommerce.entities.category.CategoryMetadataFieldValue;

@Repository
public interface CategoryMetadataFieldValueRepository extends JpaRepository<CategoryMetadataFieldValue, Long> {


}