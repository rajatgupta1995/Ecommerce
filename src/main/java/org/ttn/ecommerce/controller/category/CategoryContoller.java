package org.ttn.ecommerce.controller.category;

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
     *  @Task API add metadata field
     */
    @PostMapping("/add/metadata_field")
    public ResponseEntity<?> addMetadataField(@RequestBody CategoryMetaDataFieldDto categoryMetaDataFieldDto){
        return categoryService.addMetadataField(categoryMetaDataFieldDto);
    }

    /**
     *  @Task API to get metadata field
     */
    @GetMapping("/all/metadata_field")
    public ResponseEntity<?> viewAllMetadataField(){
        return categoryService.getMetadataField();
    }

    /**
     *  @Task API add a category
     */
    @PostMapping("/add/category")
    public ResponseEntity<?> addCategory(@RequestBody CategoryDto categoryDto) {
        return categoryService.addCategory(categoryDto);
    }

    /**
     *  @Task API view all category
     */
    @GetMapping("/all/categories")
    public ResponseEntity<?> allCategories() {

        return categoryService.viewAllCategories();
    }

    /**
     *  @Task API view category by id
     */
    @GetMapping("/category")
    public ResponseEntity<CategoryViewDto> viewCategory(@RequestParam long categoryId) {

        return new ResponseEntity<>(categoryService.viewCategory(categoryId), HttpStatus.OK);
    }

    /**
     *  @Task API to update category
     */
    @PutMapping(path = "/update/category")
    public ResponseEntity<?> updateCategory(@RequestParam long categoryId,@RequestBody CategoryUpdateDto categoryUpdateDto){
        return categoryService.updateCategory(categoryId,categoryUpdateDto);
    }

    /**
     *  @Task API to add new category metadata field value for a category
     */
    @PostMapping("/metadata-fields/addValues")
    public ResponseEntity<?> addMetaDataFieldValues(@RequestBody CategoryMetaDataFieldValueDto categoryMetaDataFieldValueDto,  @RequestParam Long categoryId, @RequestParam Long metaFieldId) {
        return categoryService.addNewMetadataFieldValues(categoryMetaDataFieldValueDto, categoryId, metaFieldId);
    }

    /**
     * @Task API to add update category metadata field value for a category
     */
    @PutMapping("/metadata-fields/updateValues")
    public ResponseEntity<?> updateCategoryMetadataFieldValues(@RequestParam Long categoryId, @RequestParam Long metaFieldId, @RequestBody CategoryMetaDataFieldValueDto categoryMetaDataFieldValueDto) {
        return categoryService.updateCategoryMetadataFieldValues(categoryId, metaFieldId,categoryMetaDataFieldValueDto);
    }


}
