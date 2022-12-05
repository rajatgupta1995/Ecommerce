package org.ttn.ecommerce.services.Impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.ttn.ecommerce.dto.category.*;
import org.ttn.ecommerce.entity.category.Category;
import org.ttn.ecommerce.entity.category.CategoryMetaDataField;
import org.ttn.ecommerce.entity.category.CategoryMetadataFieldValue;
import org.ttn.ecommerce.exception.BadRequestException;
import org.ttn.ecommerce.exception.CategoryNotFoundException;
import org.ttn.ecommerce.exception.ResourceNotFoundException;
import org.ttn.ecommerce.exception.UserAlreadyExistsException;
import org.ttn.ecommerce.repository.productrepository.ProductRepository;
import org.ttn.ecommerce.repository.categoryrepository.CategoryMetaDataFieldRepository;
import org.ttn.ecommerce.repository.categoryrepository.CategoryMetadataFieldValueRepository;
import org.ttn.ecommerce.repository.categoryrepository.CategoryRepository;
import org.ttn.ecommerce.services.CategoryService;
import org.ttn.ecommerce.utils.StringToSetParser;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional
public class CategoryServiceImpl implements CategoryService {
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private CategoryMetaDataFieldRepository categoryMetadataFieldRepository;
    @Autowired
    private CategoryMetadataFieldValueRepository categoryMetadataFieldValuesRepository;

    @Autowired
    private ProductRepository productRepository;

    //to add metadata field
    @Override
    public ResponseEntity<?> addMetadataField(CategoryMetaDataFieldDto categoryMetaDataFieldDto) {
        Optional<CategoryMetaDataField> categoryMetaDataField = categoryMetadataFieldRepository.findByName(categoryMetaDataFieldDto.getName());
        if (categoryMetaDataField.isPresent()) {
            log.info("MetaData already exists.");
            throw new UserAlreadyExistsException("MetaData Field Already Exists");
        }
        CategoryMetaDataField categoryMetaDataField1 = new CategoryMetaDataField();
        categoryMetaDataField1.setName(categoryMetaDataFieldDto.getName());
        categoryMetadataFieldRepository.save(categoryMetaDataField1);
        log.info("New Metadata field created");
        return new ResponseEntity<>("MetaData Field Added.", HttpStatus.CREATED);
    }

    //to get all metadata field
    @Override
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

    //to add new category
    @Override
    public ResponseEntity<?> addCategory(CategoryDto categoryDto) {
        String categoryName =categoryDto.getName();
        Long  parentCategoryId= categoryDto.getParentCategoryId();
        Category parent=null;
        if(parentCategoryId != null){
            parent = categoryRepository.findById(parentCategoryId)
                    .orElseThrow(()->new CategoryNotFoundException("Parent Category Id Is Not Valid!"));

//            if(productRepository.existsByCategoryId(parent.getId())>0){
//                return new ResponseEntity<>("Parent Category Should Not Associated With Any Product",HttpStatus.BAD_REQUEST);
//            }

        }

        Optional<Category> category = categoryRepository.findByCategoryName(categoryName);
        if(category.isPresent()){
            return new ResponseEntity<>("Category Already Exists",HttpStatus.BAD_REQUEST);
        }

        Category category1 = new Category();
        category1.setName(categoryDto.getName());
        category1.setParentCategory(parent);

        categoryRepository.save(category1);

        return new ResponseEntity<>("Category Created",HttpStatus.CREATED);

    }



    @Override
    public ResponseEntity<List<SubCategoryDto>> viewAllCategories(){
        List<Category> categories= categoryRepository.findAll();
        List<SubCategoryDto> subCategoryDtoList = new ArrayList<>();
        for (Category category : categories){
            SubCategoryDto subCategoryDto = new SubCategoryDto();
            subCategoryDto.setId(category.getId());
            subCategoryDto.setName(category.getName());

            subCategoryDtoList.add(subCategoryDto);
        }
        return new ResponseEntity<>(subCategoryDtoList,HttpStatus.OK);
    }




    @Override
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

    @Override
    public ResponseEntity<?> updateCategory(long categoryId, CategoryUpdateDto categoryUpdateDto) {
        if (categoryRepository.existsById(categoryId)) {
            log.info("category exists");
            Category category = categoryRepository.getById(categoryId);
            Optional<Category> categoryDuplicate = categoryRepository.findByCategoryName(categoryUpdateDto.getName());
            if (!categoryDuplicate.isPresent()) {
                category.setName(categoryUpdateDto.getName());
                categoryRepository.save(category);
                return new ResponseEntity<>("Saved category with updated name: "+category.getName(), HttpStatus.CREATED);
            } else {
                return new ResponseEntity<>("You cannot create duplicate categories!", HttpStatus.BAD_REQUEST);
            }
        } else {
            return new ResponseEntity<>("No Category exists with this categoryId: "+categoryId, HttpStatus.NOT_FOUND);
        }
    }

    @Override
    public ResponseEntity<?> addNewMetadataFieldValues(CategoryMetaDataFieldValueDto categoryMetaDataFieldValueDto, Long categoryId, Long metaFieldId){

        Optional<Category> category= categoryRepository.findById(categoryId);
        Optional<CategoryMetaDataField> categoryMetadataField= categoryMetadataFieldRepository.findById(metaFieldId);
        if (!category.isPresent())
            throw new ResourceNotFoundException("Category does not exists");
        else if (!categoryMetadataField.isPresent())
            throw new ResourceNotFoundException("Metadata field does not exists");
        else if(categoryMetaDataFieldValueDto.getValues().isEmpty()){
            return new ResponseEntity<>("Fields should have at least one value",HttpStatus.BAD_REQUEST);
        }else{
            Category category1= new Category();
            category1= category.get();

            CategoryMetaDataField categoryMetadataField1= new CategoryMetaDataField();
            categoryMetadataField1= categoryMetadataField.get();

            //Logic
            CategoryMetadataFieldValue categoryFieldValues = new CategoryMetadataFieldValue();

            String values = StringToSetParser.toCommaSeparatedString(categoryMetaDataFieldValueDto.getValues());

            categoryFieldValues.setValue(values);
            categoryFieldValues.setCategory(category1);
            categoryFieldValues.setCategoryMetaDataField(categoryMetadataField1);

            categoryMetadataFieldValuesRepository.save(categoryFieldValues);
            return new ResponseEntity<>("Metadata field values added successfully",HttpStatus.OK);
        }

    }

    @Override
    public ResponseEntity<?> updateCategoryMetadataFieldValues(Long categoryId, Long metadataFieldId, CategoryMetaDataFieldValueDto categoryMetaDataFieldValueDto) {
        Optional<Category> category= categoryRepository.findById(categoryId);

        Optional<CategoryMetaDataField> categoryMetadataField= categoryMetadataFieldRepository.findById(metadataFieldId);
        if (!category.isPresent()) {
            log.info("category does not exists");
            throw new ResourceNotFoundException("Category does not exists");
        }else if (!categoryMetadataField.isPresent()) {
            log.info("category metadata does not exist");
            throw new ResourceNotFoundException("Metadata field does not exists");
        }else{
                Category category1=category.get();
                log.info("category exists");
                CategoryMetaDataField categoryMetaDataField1 = categoryMetadataField.get();
                log.info("category metadata exist");

                //Logic
                CategoryMetadataFieldValue categoryMetadataFieldValues = categoryMetadataFieldValuesRepository.findByCategoryMetadataFieldId(metadataFieldId).orElseThrow(()->new CategoryNotFoundException(
                        "Category MetaData Not Found For CategoryId -> "
                                +categoryId + "And MetaDataField Value ID -> " + metadataFieldId));

               String values = categoryMetaDataFieldValueDto.getValues().stream().collect(Collectors.joining(","));


               categoryMetadataFieldValues.setValue(values);
               categoryMetadataFieldValuesRepository.save(categoryMetadataFieldValues);

                return new ResponseEntity<>("Updated the passed values to category metadata field: "+ categoryMetaDataField1.getName(), HttpStatus.CREATED);
            }
        }

    public List<SellerCategoryResponseDTO> viewSellerCategory(){
        List<Category> categoryList = categoryRepository.findAll();
        List<SellerCategoryResponseDTO> responseList = new ArrayList<>();
        for(Category category: categoryList){
            if(category.getSubCategory().isEmpty()){

                List<CategoryMetadataFieldValue> categoryMetadataList =
                        categoryMetadataFieldValuesRepository.findByCategory(category);

                SellerCategoryResponseDTO sellerResponse = new SellerCategoryResponseDTO();

                sellerResponse.setId(category.getId());
                sellerResponse.setName(category.getName());

                CategoryDto categoryDto=new CategoryDto();

                categoryDto.setName(category.getParentCategory().getName());
                categoryDto.setParentCategoryId(category.getParentCategory().getId());

                sellerResponse.setParent(categoryDto);

                List<MetadataResponseDTO> metaList = new ArrayList<>();

                for (CategoryMetadataFieldValue metadata: categoryMetadataList){
                    MetadataResponseDTO metadataResponseDTO = new MetadataResponseDTO();
                    metadataResponseDTO.setMetadataId(metadata.getCategoryMetaDataField().getId());
                    metadataResponseDTO.setFieldName(metadata.getCategoryMetaDataField().getName());
                    metadataResponseDTO.setPossibleValues(metadata.getValue());
                    metaList.add(metadataResponseDTO);
                }
                sellerResponse.setMetadata(metaList);
                responseList.add(sellerResponse);
            }
        }
        return responseList;
    }

    public List<Category> viewCustomerCategory(Optional<Integer> optionalId){
        if(optionalId.isPresent()){
            // if ID is present, fetch its immediate children
            Category category = categoryRepository.findById((long)optionalId.get()).orElseThrow(() -> new BadRequestException("Invalid id"));
            List<Category> childList = category.getSubCategory();
            return childList;
        }
        else{
            // if ID isn't provided fetch all root nodes
            List<Category> categoryList = categoryRepository.findAll();
            List<Category> rootNodes = new ArrayList<>();
            // filtering rootNodes
            for(Category category: categoryList){
                if(category.getParentCategory()==null){
                    rootNodes.add(category);
                }
            }
            return rootNodes;
        }
    }
}
