package org.ttn.ecommerce.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.ttn.ecommerce.dto.category.CategoryMetaDataFieldDto;
import org.ttn.ecommerce.dto.category.CategoryDto;
import org.ttn.ecommerce.dto.category.CategoryViewDto;
import org.ttn.ecommerce.entities.category.Category;
import org.ttn.ecommerce.repository.category.CategoryRepository;
import org.ttn.ecommerce.services.CategoryService;

import java.util.List;

@RestController
@RequestMapping("/category/admin")
public class CategoryContoller {
    CategoryRepository categoryRepository;
    @Autowired
    CategoryService categoryService;

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
    @GetMapping("/add/category")
    public ResponseEntity<?> addCategory(@RequestBody CategoryDto categoryModel) {
        return categoryService.addCategory(categoryModel);
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
}
