package org.ttn.ecommerce.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.ttn.ecommerce.dto.category.*;
import org.ttn.ecommerce.services.CategoryService;

@RestController
@RequestMapping("/admin/category")
public class CategoryContoller {
    @Autowired
    private CategoryService categoryService;

    /**
     *  API add metadata field
     */
    @PostMapping("/add/metadata_field")
    public ResponseEntity<?> addMetadataField(@RequestBody CategoryMetaDataFieldDto categoryMetaDataFieldDto){
        return categoryService.addMetadataField(categoryMetaDataFieldDto);
    }

    /**
     *  API to get metadata field
     */
    @GetMapping("/all/metadata_field")
    public ResponseEntity<?> viewAllMetadataField(){
        return categoryService.getMetadataField();
    }

    /**
     *  API add a category
     */
    @PostMapping("/add/category")
    public ResponseEntity<?> addCategory(@RequestBody CategoryDto categoryDto) {
        return categoryService.addCategory(categoryDto);
    }

    /**
     *  API view all category
     */
    @GetMapping("/all/categories")
    public ResponseEntity<?> allCategories() {

        return categoryService.viewAllCategories();
    }

    /**
     *  API view category by id
     */
    @GetMapping("/category/{id}")
    public ResponseEntity<CategoryViewDto> viewCategory(@PathVariable long id) {

        return new ResponseEntity<>(categoryService.viewCategory(id), HttpStatus.OK);
    }

    /**
     *  API to update category
     */
    @PutMapping(path = "/update/category")
    public ResponseEntity<?> updateCategory(@RequestParam long categoryId,@RequestBody CategoryUpdateDto categoryUpdateDto){
        return categoryService.updateCategory(categoryId,categoryUpdateDto);
    }

    /**
     *  API to add new category metadata field value for a category
     */
    @PostMapping("/metadata-fields/addValues/{categoryId}/{metaFieldId}")
    public String addMetaDataFieldValues(@RequestBody CategoryMetaDataFieldValueDto categoryMetaDataFieldValueDto,  @PathVariable Long categoryId, @PathVariable Long metaFieldId) {
        return categoryService.addNewMetadataFieldValues(categoryMetaDataFieldValueDto, categoryId, metaFieldId);
    }

    /**
     *  API to add update category metadata field value for a category
     */
    @PutMapping("/metadata-fields/updateValues/{categoryId}/{metaFieldId}")
    public ResponseEntity<?> updateCategoryMetadataFieldValues(@PathVariable Long categoryId, @PathVariable Long metaFieldId, @RequestBody CategoryMetaDataFieldValueDto categoryMetaDataFieldValueDto) {
        return categoryService.updateCategoryMetadataFieldValues(categoryId, metaFieldId,categoryMetaDataFieldValueDto);
    }
}
