package org.ttn.ecommerce.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.ttn.ecommerce.dto.category.CategoryMetaDataFieldDto;
import org.ttn.ecommerce.dto.category.CategoryDto;
import org.ttn.ecommerce.dto.category.CategoryViewDto;
import org.ttn.ecommerce.dto.category.SubCategoryDto;
import org.ttn.ecommerce.entities.category.Category;
import org.ttn.ecommerce.entities.category.CategoryMetaDataField;
import org.ttn.ecommerce.exception.ResourceNotFoundException;
import org.ttn.ecommerce.exception.UserAlreadyExistsException;
import org.ttn.ecommerce.repository.category.CategoryMetaDataFieldRepository;
import org.ttn.ecommerce.repository.category.CategoryMetadataFieldValueRepository;
import org.ttn.ecommerce.repository.category.CategoryRepository;

import java.util.*;

@Service
@Slf4j
@Transactional
public class CategoryService {
    @Autowired
    CategoryRepository categoryRepository;
    @Autowired
    CategoryMetaDataFieldRepository categoryMetadataFieldRepository;
    @Autowired
    CategoryMetadataFieldValueRepository categoryMetadataFieldValuesRepository;

    public ResponseEntity<?> addMetadataField(CategoryMetaDataFieldDto categoryMetaDataFieldDto) {
        Optional<CategoryMetaDataField> categoryMetaDataField = categoryMetadataFieldRepository.findByName(categoryMetaDataFieldDto.getName());
        if (categoryMetaDataField.isPresent()) {
            log.info("MetaData already exists.");
            throw new UserAlreadyExistsException("MetaData Field Already Exists");
        }
        CategoryMetaDataField categoryMetaDataField1 = new CategoryMetaDataField();
        categoryMetaDataField1.setName(categoryMetaDataField1.getName());
        categoryMetadataFieldRepository.save(categoryMetaDataField1);
        log.info("New Metadata field created");
        return new ResponseEntity<>("MetaData Field Added.", HttpStatus.CREATED);
    }

    public ResponseEntity<?> getMetadataField() {
        List<CategoryMetaDataField> list = categoryMetadataFieldRepository.findAll();
        log.info("returning a list of metadata field.");
        List<CategoryMetaDataFieldDto> responseFieldList = new ArrayList<>();
        list.forEach(field -> {
            CategoryMetaDataFieldDto categoryMetaDataFieldDto = new CategoryMetaDataFieldDto();
            categoryMetaDataFieldDto.setName(field.getName());
            categoryMetaDataFieldDto.setId(field.getId());
            responseFieldList.add(categoryMetaDataFieldDto);
        });
        return new ResponseEntity<>(responseFieldList, HttpStatus.OK);
    }


    public ResponseEntity<?> viewAllCategories(){

        return new ResponseEntity<>((List<Category>) categoryRepository.findAll(),HttpStatus.OK);
    }

    public CategoryViewDto viewCategory(long id) {
        Category category = categoryRepository.findById(id).orElseThrow(() -> new IllegalStateException("Id not found."));

        CategoryViewDto categoryViewDto = new CategoryViewDto();
        categoryViewDto.setId(category.getId());
        categoryViewDto.setName(category.getName());
        categoryViewDto.setParent(category.getParentCategory());


        Set<SubCategoryDto> childList = new HashSet<>();

        for(Category child: category.getSubCategory()){
            SubCategoryDto childCategoryDto = new SubCategoryDto();
            childCategoryDto.setId(child.getId());
            childCategoryDto.setName(child.getName());
            childList.add(childCategoryDto);

        }
        categoryViewDto.setChildren(childList);
        return categoryViewDto;

    }

}
