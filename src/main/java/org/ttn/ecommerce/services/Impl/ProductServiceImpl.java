package org.ttn.ecommerce.services.Impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.ttn.ecommerce.dto.product.ProductDto;
import org.ttn.ecommerce.dto.product.ProductUpdateDto;
import org.ttn.ecommerce.dto.product.ProductViewDto;
import org.ttn.ecommerce.entities.category.Category;
import org.ttn.ecommerce.entities.product.Product;
import org.ttn.ecommerce.entities.register.Seller;
import org.ttn.ecommerce.exception.ResourceNotFoundException;
import org.ttn.ecommerce.exception.UserNotFoundException;
import org.ttn.ecommerce.repository.ProductRepository.ProductRepository;
import org.ttn.ecommerce.repository.RegisterRepository.SellerRepository;
import org.ttn.ecommerce.repository.RegisterRepository.UserRepository;
import org.ttn.ecommerce.repository.TokenRepository.AccessTokenRepository;
import org.ttn.ecommerce.repository.categoryRepository.CategoryRepository;
import org.ttn.ecommerce.services.EmailService;
import org.ttn.ecommerce.services.ProductService;
import org.ttn.ecommerce.services.TokenService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

    @Override
    public ProductViewDto viewProduct(String sellerEmail, Long prodId){
        Product product = productRepository.findById(prodId).orElseThrow(
                () -> new ResourceNotFoundException("Product does not exists with this id:"+prodId));
        Optional<Seller> seller=sellerRepository.findByEmail(sellerEmail);
        if(!seller.isPresent())
            throw new ResourceNotFoundException("Seller not found");

        boolean isProductExists = product.getSeller().getId() == seller.get().getId();
        if(!isProductExists){
            throw new ResourceNotFoundException("Only Seller has access to product");
        }
        ProductViewDto productDto = new ProductViewDto();
        productDto.setCategory(product.getCategory());
        productDto.setName(product.getName());
        productDto.setBrand(product.getBrand());
        productDto.setDescription(product.getDescription());
        productDto.setProductId(product.getId());
        productDto.setActive(product.isActive());
        return productDto;
    }

    public List<ProductViewDto> viewProducts(String sellerEmail){
        Optional<Seller> seller = sellerRepository.findByEmail(sellerEmail);
        if(!seller.isPresent()){
            throw new UserNotFoundException("Seller not found");
        }
        List<Product> productList=productRepository.findBySeller(seller.get());
        if(productList.isEmpty()){
            throw new ResourceNotFoundException("Only Seller has access to product");
        }
        List<ProductViewDto> productViewDtoList=new ArrayList<>();
        productList.forEach((product)->{
            ProductViewDto productDto = new ProductViewDto();
            productDto.setCategory(product.getCategory());
            productDto.setName(product.getName());
            productDto.setBrand(product.getBrand());
            productDto.setDescription(product.getDescription());
            productDto.setProductId(product.getId());
            productDto.setActive(product.isActive());
            productViewDtoList.add(productDto);
        });
        return productViewDtoList;
    }

    public String removeProduct(String sellerEmail, Long productId) {
        Product product = productRepository.findById(productId).orElseThrow(
                () -> new ResourceNotFoundException("Product does not exist."));
        Optional<Seller> seller=sellerRepository.findByEmail(sellerEmail);
        if(!seller.isPresent())
            throw new ResourceNotFoundException("Seller not found");
        boolean isProductExists = product.getSeller().getId() == seller.get().getId();
        if(!isProductExists){
            throw new ResourceNotFoundException("Only Seller has access to product");
        }
        productRepository.delete(product);
        return "Product deleted successfully";
    }

    public String updateProduct(String sellerEmail, Long prodId, ProductUpdateDto updateProduct){
        Product savedProduct=productRepository.findById(prodId).orElseThrow(
                ()-> new ResourceNotFoundException("Product does not exists with this id:"+prodId));

        Optional<Seller> seller=sellerRepository.findByEmail(sellerEmail);
        if(!seller.isPresent())
            throw new ResourceNotFoundException("Seller not found");
        boolean isProductExists = savedProduct.getSeller().getId()==seller.get().getId();
        if(!isProductExists){
            throw new ResourceNotFoundException("Only Seller has access to product");
        }

        if (updateProduct.getName() != null)
            savedProduct.setName(updateProduct.getName());

        if (updateProduct.getName() != null)
            savedProduct.setBrand(updateProduct.getName());

        if (updateProduct.getDescription() != null)
            savedProduct.setDescription(updateProduct.getDescription());

        if (updateProduct.getIsCancellable() != null)
            savedProduct.setCancellable(updateProduct.getIsCancellable());

        if (updateProduct.getIsReturnable() != null)
            savedProduct.setReturnable(updateProduct.getIsReturnable());

        return "Product Updated Successfully";
    }
}
