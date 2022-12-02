package org.ttn.ecommerce.services;

import org.springframework.http.ResponseEntity;
import org.ttn.ecommerce.dto.category.*;

public interface CategoryService {
    //to add metadata field
    ResponseEntity<?> addMetadataField(CategoryMetaDataFieldDto categoryMetaDataFieldDto);

    //to get all metadata field
    ResponseEntity<?> getMetadataField();

    //to add new category
    ResponseEntity<?> addCategory(CategoryDto categoryDto);

    ResponseEntity<?> viewAllCategories();

    CategoryViewDto viewCategory(long id);

    ResponseEntity<?> updateCategory(long categoryId, CategoryUpdateDto categoryUpdateDto);

    String addNewMetadataFieldValues(CategoryMetaDataFieldValueDto categoryMetaDataFieldValueDto, Long categoryId, Long metaFieldId);

    ResponseEntity<?> updateCategoryMetadataFieldValues(Long categoryId, Long metadataFieldId, CategoryMetaDataFieldValueDto categoryMetaDataFieldValueDto);
}
