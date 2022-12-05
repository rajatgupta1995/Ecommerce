package org.ttn.ecommerce.services.Impl;

import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.ttn.ecommerce.dto.category.CategoryDto;
import org.ttn.ecommerce.dto.category.ProductVariationDto;
import org.ttn.ecommerce.dto.product.*;
import org.ttn.ecommerce.entity.category.Category;
import org.ttn.ecommerce.entity.category.CategoryMetadataFieldValue;
import org.ttn.ecommerce.entity.product.Product;
import org.ttn.ecommerce.entity.product.ProductVariation;
import org.ttn.ecommerce.entity.register.Seller;
import org.ttn.ecommerce.exception.*;
import org.ttn.ecommerce.repository.categoryrepository.CategoryMetadataFieldValueRepository;
import org.ttn.ecommerce.repository.categoryrepository.CategoryRepository;
import org.ttn.ecommerce.repository.productrepository.ProductRepository;
import org.ttn.ecommerce.repository.productrepository.ProductVariationRepository;
import org.ttn.ecommerce.repository.registerrepository.SellerRepository;
import org.ttn.ecommerce.repository.registerrepository.UserRepository;
import org.ttn.ecommerce.repository.tokenrepository.AccessTokenRepository;
import org.ttn.ecommerce.services.ProductService;
import org.ttn.ecommerce.services.TokenService;

import javax.transaction.Transactional;
import java.util.*;

@Service
@Slf4j
public class ProductServiceImpl implements ProductService {
    @Autowired
    private TokenService tokenService;
    @Autowired
    private AccessTokenRepository accessTokenRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private SellerRepository sellerRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private EmailService emailService;
    @Autowired
    private ProductVariationRepository productVariationRepository;
    @Autowired
    private CategoryMetadataFieldValueRepository categoryMetadataFieldValueRepository;

    /**
     *
     * @param sellerEmail
     * @param productDto
     * @param category_id
     * @return
     */
    @Override
    public ResponseEntity<?> addProduct(String sellerEmail, ProductDto productDto, Long category_id) {
        if (categoryRepository.existsById(category_id)) {
            Category category = categoryRepository.findById(category_id).get();
            if(category.getSubCategory().size()!=0) {
                log.info("cannot create a product of Parent Category");
                return new ResponseEntity<>("You cannot create a product of Parent Category node!", HttpStatus.BAD_REQUEST);
            }else {
                Optional<Seller> seller = sellerRepository.findByEmail(sellerEmail);
                if(!seller.isPresent())
                    throw new ResourceNotFoundException("Seller not found");
                log.info("user found"+seller.get().getFirstName());
                if (productRepository.findExistingProduct(productDto.getName(), seller.get().getId(), productDto.getBrand(), category_id).isPresent()) {
                    throw new UserAlreadyExistsException("Product exists with this name");
                }
                Product product = new Product();
                product.setCategory(category);
                product.setBrand(productDto.getBrand());
                product.setName(productDto.getName());
                if(productDto.getDescription()!=null)
                    product.setDescription(productDto.getDescription());
                product.setActive(false);
                if(productDto.getIs_cancellable()!=null)
                    product.setCancellable(productDto.getIs_cancellable());
                else {
                    product.setCancellable(false);
                }
                if(productDto.getIs_returnable()!=null)
                    product.setReturnable(productDto.getIs_returnable());
                else {
                    product.setCancellable(false);
                }
                product.setSeller(seller.get());
                product = productRepository.save(product);
                ;
                String Subject="Product Added";
                String message="A new product has been added, It is inactive at the moment. Below are the details of added Product"
                        + "\nProduct name: "+product.getName()
                        + "\nBrand: "+product.getBrand()
                        + "\nCategory: "+product.getCategory().getName()
                        + "\nDescription: "+product.getDescription()
                        + "\nSeller: "+seller.get().getEmail()
                        + "\nActivate the Product.";
                String emailTo="rajat.gupta1@tothenew.com";
                emailService.sendEmail(emailTo,Subject,message);
                log.info("Product added to seller");
                return new ResponseEntity<>("Product added to Seller: "+seller.get().getFirstName(), HttpStatus.CREATED);
            }
        }else {
            log.info("category does not exists.");
            throw new ResourceNotFoundException("Category does not exists");
        }
    }

    /**
     *
     * @param sellerEmail
     * @param prodId
     * @return
     */
    /* To view product with particular id*/
    @Override
    public ProductViewDto viewProduct(String sellerEmail, Long prodId){
        /* check if Id is valid */
        Product product = productRepository.findById(prodId).orElseThrow(
                () -> new ResourceNotFoundException("Product does not exists with this id:"+prodId));
        Optional<Seller> seller=sellerRepository.findByEmail(sellerEmail);
        /* Check seller exists with given sellerEmail */
        if(!seller.isPresent())
            throw new ResourceNotFoundException("Seller not found");
        /* Logged in seller should be the creator of product */
        boolean isProductExists = product.getSeller().getId() == seller.get().getId();
        if(!isProductExists){
            throw new ResourceNotFoundException("Only Seller has access to product");
        }
        ProductViewDto productDto = new ProductViewDto();

        CategoryDto categoryDto=new CategoryDto();

        categoryDto.setParentCategoryId(product.getCategory().getId());
        categoryDto.setName(product.getCategory().getName());

        productDto.setCategory(categoryDto);
        productDto.setName(product.getName());
        productDto.setBrand(product.getBrand());
        productDto.setDescription(product.getDescription());
        productDto.setProductId(product.getId());
        productDto.setActive(product.isActive());
        productDto.setCancellable(product.isCancellable());
        productDto.setReturnable(product.isReturnable());
        return productDto;
    }

    /**
     *
     * @param sellerEmail
     * @return
     */
    /* To view products of logged in seller */
    @Override
    public List<ProductViewDto> viewProducts(String sellerEmail){
        /* Check seller exists with given sellerEmail */
        Optional<Seller> seller = sellerRepository.findByEmail(sellerEmail);
        if(!seller.isPresent()){
            throw new UserNotFoundException("Seller not found");
        }

        List<Product> productList=productRepository.findBySeller(seller.get());
        if(productList.isEmpty()){
            throw new ResourceNotFoundException("Seller has no product");
        }
        List<ProductViewDto> productViewDtoList=new ArrayList<>();
        productList.forEach((product)->{
            ProductViewDto productDto = new ProductViewDto();

            CategoryDto categoryDto=new CategoryDto();

            categoryDto.setParentCategoryId(product.getCategory().getId());
            categoryDto.setName(product.getCategory().getName());

            productDto.setCategory(categoryDto);

            productDto.setName(product.getName());
            productDto.setBrand(product.getBrand());
            productDto.setDescription(product.getDescription());
            productDto.setProductId(product.getId());
            productDto.setActive(product.isActive());
            productDto.setCancellable(product.isCancellable());
            productDto.setReturnable(product.isReturnable());
            productViewDtoList.add(productDto);
        });
        return productViewDtoList;
    }

    /**
     *
     * @param sellerEmail
     * @param productId
     * @return
     */
    /* To remove product with given productId */
    @Transactional
    @Override
    public String removeProduct(String sellerEmail, Long productId) {
        /* check if Id is valid */
        Product product = productRepository.findById(productId).orElseThrow(
                () -> new ResourceNotFoundException("Product does not exist."));
        Optional<Seller> seller=sellerRepository.findByEmail(sellerEmail);
        /* Check seller exists with given sellerEmail */
        if(!seller.isPresent())
            throw new ResourceNotFoundException("Seller not found");
        boolean isProductExists = product.getSeller().getId() == seller.get().getId();
        /* Logged in seller should be the creator of product */
        if(!isProductExists){
            throw new ResourceNotFoundException("Only Seller has access to product");
        }
        productRepository.deleteById(productId);
        return "Product deleted successfully";
    }

    /**
     *
     * @param sellerEmail
     * @param prodId
     * @param updateProduct
     * @return
     */
    /* To update product with given productId */
    @Override
    public String updateProduct(String sellerEmail, Long prodId, ProductUpdateDto updateProduct){
        /* check if Id is valid */
        Product savedProduct=productRepository.findById(prodId).orElseThrow(
                ()-> new ResourceNotFoundException("Product does not exists with this id:"+prodId));

        Optional<Seller> seller=sellerRepository.findByEmail(sellerEmail);
        /* Check seller exists with given sellerEmail */
        if(!seller.isPresent())
            throw new ResourceNotFoundException("Seller not found");
        boolean isProductExists = savedProduct.getSeller().getId()==seller.get().getId();
        /* Logged in seller be the creator of product */
        if(!isProductExists){
            throw new ResourceNotFoundException("Only Seller has access to product");
        }

        if (updateProduct.getName() != null)
            savedProduct.setName(updateProduct.getName());

        if (updateProduct.getBrand() != null)
            savedProduct.setBrand(updateProduct.getBrand());

        if (updateProduct.getDescription() != null)
            savedProduct.setDescription(updateProduct.getDescription());

        if (updateProduct.getIsCancellable() != null)
            savedProduct.setCancellable(updateProduct.getIsCancellable());

        if (updateProduct.getIsReturnable() != null)
            savedProduct.setReturnable(updateProduct.getIsReturnable());


        productRepository.save(savedProduct);
        return "Product Updated Successfully";
    }

    /**
     * Customer Part
     */

    /**
     *
     * @param productId
     * @return
     */
    public ProductCustomerResponseDto viewCustomerProduct(Long productId) {
        /* check if ID is valid */
        Optional<Product> product = productRepository.findById(productId);
        if (product.isEmpty()) {
            throw new BadRequestException("Invalid Product Id.");
        }
        /* Check if product is deleted or inactive */
        if (product.get().isDeleted() || !product.get().isActive()) {
            throw new BadRequestException("Either product is deleted or is inactive");
        }

        List<ProductVariation> productVariations = productVariationRepository.findByProduct(product.get());

//        if (productVariations.isEmpty()) {
//            throw new BadRequestException("Product variation not found");
//        }

        ProductCustomerResponseDto productCustomerResponseDto = new ProductCustomerResponseDto();
        productCustomerResponseDto.setId(product.get().getId());
        productCustomerResponseDto.setName(product.get().getName());
        productCustomerResponseDto.setBrand(product.get().getBrand());
        productCustomerResponseDto.setDescription(product.get().getDescription());
        productCustomerResponseDto.setIsActive(product.get().isActive());
        productCustomerResponseDto.setIsReturnable(product.get().isReturnable());
        productCustomerResponseDto.setIsCancellable(product.get().isCancellable());

        CategoryDto categoryDto=new CategoryDto();

        categoryDto.setParentCategoryId(product.get().getCategory().getId());
        categoryDto.setName(product.get().getCategory().getName());

        productCustomerResponseDto.setCategory(categoryDto);

        List<VariationResponseDTO> variationResponseDTOList = new ArrayList<>();
        for(ProductVariation variation: productVariations){
            VariationResponseDTO variationResponseDTO = new VariationResponseDTO();
            variationResponseDTO.setProductId(variation.getProduct().getId());
            variationResponseDTO.setId(variation.getId());
            variationResponseDTO.setPrice(variation.getPrice());
            variationResponseDTO.setQuantity(variation.getQuantityAvailable());
            variationResponseDTO.setMetadata(variation.getMetadata());
            variationResponseDTOList.add(variationResponseDTO);
        }
        productCustomerResponseDto.setVariations(variationResponseDTOList);

        return productCustomerResponseDto;
    }

    /**
     *
     * @param id
     * @return
     */
    public List<ProductCustomerResponseDto> retrieveProducts(Long id) {

        int noOfCategory = categoryRepository.findById(id).get().getSubCategory().size();
        Category category = categoryRepository.findById(id).get();
        if (noOfCategory == 0) {
            Set<Product> products = category.getProduct();
            List<ProductCustomerResponseDto> productResponseDtoList = new ArrayList<>();
            for (Product product : products) {

                ProductCustomerResponseDto productResponseDTO = new ProductCustomerResponseDto();

                productResponseDTO.setId(product.getId());
                productResponseDTO.setName(product.getName());
                productResponseDTO.setBrand(product.getBrand());
                productResponseDTO.setDescription(product.getDescription());
                productResponseDTO.setIsActive(product.isActive());
                productResponseDTO.setIsCancellable(product.isCancellable());
                productResponseDTO.setIsReturnable(product.isReturnable());
                productResponseDTO.setIsCancellable(product.isCancellable());

                CategoryDto categoryDto=new CategoryDto();

                categoryDto.setParentCategoryId(product.getCategory().getId());
                categoryDto.setName(product.getCategory().getName());

                productResponseDTO.setCategory(categoryDto);
                productResponseDtoList.add(productResponseDTO);
            }
            return productResponseDtoList;

        } else {

            List<ProductCustomerResponseDto> allProducts = new ArrayList<>();
            for (Category category1 : category.getSubCategory()) {

                List<ProductCustomerResponseDto> currProducts = retrieveProducts(category1.getId());

                for (ProductCustomerResponseDto productResponseDto : currProducts) {

                    allProducts.add(productResponseDto);

                }

            }
            return allProducts;
        }
    }

    /**
     *
     * @param categoryId
     * @return
     */
    @Override
    public List<ProductCustomerResponseDto> customerViewAllProducts(Long categoryId) {
        return retrieveProducts(categoryId);
        /* check if category ID is valid */
//        Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new BadRequestException("Invalid category Id"));
//
//        /* check if category is a leaf node */
//        if (category.getSubCategory().isEmpty()) {
//            throw new BadRequestException("This category is not a leaf node.");
//        }
//
//        List<Product> products = productRepository.findByCategory(category);
//        /* check if products are present */
//        if (products.isEmpty()) {
//            throw new BadRequestException("Product not found");
//        }
//        List<ProductCustomerResponseDto> productResponseDTOList = new ArrayList<>();
//        for (Product product : products) {
//
//            ProductCustomerResponseDto productResponseDTO = new ProductCustomerResponseDto();
//
//            productResponseDTO.setId(product.getId());
//            productResponseDTO.setName(product.getName());
//            productResponseDTO.setBrand(product.getBrand());
//            productResponseDTO.setDescription(product.getDescription());
//            productResponseDTO.setIsActive(product.isActive());
//            productResponseDTO.setIsCancellable(product.isCancellable());
//            productResponseDTO.setIsReturnable(product.isReturnable());
//            productResponseDTO.setIsCancellable(product.isCancellable());
//
//            CategoryDto categoryDto=new CategoryDto();
//
//            categoryDto.setParentCategoryId(product.getCategory().getId());
//            categoryDto.setName(product.getCategory().getName());
//
//            productResponseDTO.setCategory(categoryDto);
//            productResponseDTOList.add(productResponseDTO);
//        }
//        return productResponseDTOList;

    }

    /**
     * @param productId
     * @return
     */
    public List<ProductCustomerResponseDto> viewSimilarProducts(Long productId) {

        /* check if ID is valid */
        Optional<Product> product = productRepository.findById(productId);
        if (product.isEmpty()) {
            throw new BadRequestException("Invalid product id");
        }

        /* check product state */
        if (product.get().isDeleted() || product.get().isActive() == false) {
            throw new BadRequestException("Product is either inactive or deleted");
        }
        // fetch similar products
        Category associatedCategory = product.get().getCategory();
        List<ProductCustomerResponseDto> similarProducts = new ArrayList<>();

        // add other products associated to its category to similar list
        List<Product> siblingProducts = productRepository.findByCategory(associatedCategory);
        for(Product product1:  siblingProducts){
            ProductCustomerResponseDto productCustomerResponseDto=new ProductCustomerResponseDto();
            productCustomerResponseDto.setId(product1.getId());
            productCustomerResponseDto.setName(product1.getName());
            productCustomerResponseDto.setBrand(product1.getBrand());
            productCustomerResponseDto.setDescription(product1.getDescription());
            productCustomerResponseDto.setIsActive(product1.isActive());
            productCustomerResponseDto.setIsReturnable(product1.isReturnable());
            productCustomerResponseDto.setIsCancellable(product1.isCancellable());

            CategoryDto categoryDto=new CategoryDto();

            categoryDto.setName(product1.getCategory().getName());
            categoryDto.setParentCategoryId(product1.getCategory().getId());
            productCustomerResponseDto.setCategory(categoryDto);

            similarProducts.add(productCustomerResponseDto);
        }
        if (similarProducts.size() == 1) {
            throw new BadRequestException("Similar products not found");
        }
        return similarProducts;
    }

    /**
     * Admin Part
     */


    /**
     *
     * @param productId
     * @return
     */
    /* To view product with particular productId*/
    @Override
    public ProductViewDto adminViewProduct(Long productId){
        /* check if Id is valid */
        Optional<Product> product1 = productRepository.findById(productId);
        if (!product1.isPresent()) {
            throw new BadRequestException("Invalid product id");
        }

        Product product=product1.get();

        ProductViewDto productViewDto = new ProductViewDto();
        productViewDto.setProductId(product.getId());
        productViewDto.setName(product.getName());
        productViewDto.setBrand(product.getBrand());
        productViewDto.setDescription(product.getDescription());
        productViewDto.setActive(product.isActive());
        productViewDto.setReturnable(product.isReturnable());
        productViewDto.setCancellable(product.isCancellable());

        CategoryDto categoryDto=new CategoryDto();

        categoryDto.setParentCategoryId(product.getCategory().getId());
        categoryDto.setName(product.getCategory().getName());

        productViewDto.setCategory(categoryDto);

        return productViewDto;
    }

    /* To view all products */
    @Override
    public List<ProductViewDto> adminViewAllProducts(){
        List<Product> products = productRepository.findAll();
        if(products.isEmpty()){
            throw new BadRequestException("Invalid Product Id.");
        }

        List<ProductViewDto> productResponseDTOList= new ArrayList<>();
        for(Product product: products){

            ProductViewDto productViewDto = new ProductViewDto();

            productViewDto.setProductId(product.getId());
            productViewDto.setName(product.getName());
            productViewDto.setBrand(product.getBrand());
            productViewDto.setDescription(product.getDescription());
            productViewDto.setActive(product.isActive());
            productViewDto.setCancellable(product.isCancellable());
            productViewDto.setReturnable(product.isReturnable());

            CategoryDto categoryDto=new CategoryDto();

            categoryDto.setParentCategoryId(product.getCategory().getId());
            categoryDto.setName(product.getCategory().getName());

            productViewDto.setCategory(categoryDto);

            productResponseDTOList.add(productViewDto);
        }
        return productResponseDTOList;
    }

    /* To activate exists and inactive product */
    @Override
    @Transactional
    public String activateProduct(Long productId) {
        /* check if Id is valid */
        Optional<Product> product = productRepository.findById(productId);
        if (product.isPresent()) {
            Product product1 = product.get();
            Seller seller = product1.getSeller();

            String emailId = seller.getEmail();
            /* Check product already active or not */
            if(!product1.isActive()){
                product1.setActive(true);
                productRepository.save(product1);
                /*send mail to seller */
                emailService.sendEmail(emailId,"Product Activated!!","Your product has been Activated");
                return "Product Activated";
            }else{
                return "Product is already Activated";
            }
        } else {
            throw new ResourceNotFoundException("Incorrect Product ID");
        }
    }

    /* To deactivate exists and active product */
    @Override
    @Transactional
    public String deactivateProduct(Long productId) {
        //check if Id is valid
        Optional<Product> product = productRepository.findById(productId);
        if (product.isPresent()) {
            Product product1 = product.get();
            Seller seller = product1.getSeller();

            String emailId = seller.getEmail();
            /* Check product already active or not */
            if(product1.isActive()) {
                product1.setActive(false);
                productRepository.save(product1);
                /* Send mail to seller */
                emailService.sendEmail(emailId,"Product Deactivated!!","Your product has been deactivated");
                return "Product Deactivated";
            } else {
                return "Product is already deactivated";
            }
        } else {
            throw new ResourceNotFoundException("Incorrect Product ID");
        }
    }

    /* Product Variation */

//    public ResponseEntity<?> createProductVariation(ProductVariationDto productVariationDto) {
//
//        ProductVariation productVariation = new ProductVariation();
//        Product product =   productRepository.findById(productVariationDto.getProductId())
//                .orElseThrow(()-> new ProductNotActiveException("Product Not Found For This Id"));
//
//        if(!product.isActive() || product.isDeleted()){
//            return new ResponseEntity<>("Product is not Active Or Product is deleted", HttpStatus.BAD_REQUEST);
//        }
//
//        Category category = product.getCategory();
//        List<CategoryMetadataFieldValue> categoryMetadataFieldValueList= categoryMetadataFieldValueRepository.findByCategoryId(category.getId());
//        Map<Object,Set<String>> meta = new LinkedHashMap<>();
//
//        for(CategoryMetadataFieldValue categoryMetadataFieldValue : categoryMetadataFieldValueList) {
//            String[] values =  categoryMetadataFieldValue.getValue().split(",");
//            List<String> list = Arrays.asList(values);
//            Set<String> listSet = new HashSet<>(list);
//
//            meta.put(categoryMetadataFieldValue.getCategoryMetaDataField().getName(),
//                    listSet);
//
//        }
//        Gson gson = new Gson();
//        String metadata = productVariationDto.getMetaData();
//        //String jsonString = "{\"key\":\"value\"}";
////Create JSON element from JSON String
//        JsonElement element = gson.fromJson(metadata, JsonElement.class);
////Fomr a Json Object from element       
//        JsonObject jsonObject = element.getAsJsonObject();
////Below line takes the 'key' from json and print its 'value'
//        System.out.println(jsonObject.get("key").getAsString());    
//
//        String metadata = productVariationDto.getMetaData();
//        JSObject jsonObj = new JSObject(metadata);
//        Iterator keys = JSONObject.keys();
//
//        while(keys.hasNext()){
//            String currentKey = (String)keys.next();
//            if (meta.get(currentKey) == null){
//                return new ResponseEntity<>("metadata value mismatch",HttpStatus.BAD_REQUEST);
//            }
//            if (!meta.get(currentKey).contains(jsonObj.getString(currentKey))){
//
//                return new ResponseEntity<>("invalid value in metadata field",HttpStatus.BAD_REQUEST);
//            }
//        }
//        productVariation.setPrice(productVariation.getPrice());
//        productVariation.setProduct(product);
//        productVariation.setMetadata(jsonObj.toString());
//        productVariation.setQuantityAvailable(productVariationDto.getQuantity());
//        productVariation.setIsActive(true);
//
//        productVariationRepository.save(productVariation);
//        return new ResponseEntity<>("product variation created successfully",HttpStatus.CREATED);
//    }
}
