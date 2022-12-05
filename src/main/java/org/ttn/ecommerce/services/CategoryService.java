package org.ttn.ecommerce.services;

import org.springframework.http.ResponseEntity;
import org.ttn.ecommerce.dto.category.*;
import org.ttn.ecommerce.entity.category.Category;

import java.util.List;
import java.util.Optional;

public interface CategoryService {
    //to add metadata field
    ResponseEntity<?> addMetadataField(CategoryMetaDataFieldDto categoryMetaDataFieldDto);

    //to get all metadata field
    ResponseEntity<?> getMetadataField();

    //to add new category
    ResponseEntity<?> addCategory(CategoryDto categoryDto);

    ResponseEntity<List<SubCategoryDto>>viewAllCategories();

    CategoryViewDto viewCategory(long id);

    ResponseEntity<?> updateCategory(long categoryId, CategoryUpdateDto categoryUpdateDto);

    ResponseEntity<?> addNewMetadataFieldValues(CategoryMetaDataFieldValueDto categoryMetaDataFieldValueDto, Long categoryId, Long metaFieldId);

    ResponseEntity<?> updateCategoryMetadataFieldValues(Long categoryId, Long metadataFieldId, CategoryMetaDataFieldValueDto categoryMetaDataFieldValueDto);

    List<SellerCategoryResponseDTO> viewSellerCategory();

    List<Category> viewCustomerCategory(Optional<Integer> optionalId);
}
