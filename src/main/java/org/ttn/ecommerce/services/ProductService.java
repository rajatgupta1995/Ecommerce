package org.ttn.ecommerce.services;

import org.springframework.http.ResponseEntity;
import org.ttn.ecommerce.dto.product.ProductCustomerResponseDto;
import org.ttn.ecommerce.dto.product.ProductDto;
import org.ttn.ecommerce.dto.product.ProductUpdateDto;
import org.ttn.ecommerce.dto.product.ProductViewDto;
import org.ttn.ecommerce.entity.product.Product;

import java.util.List;

public interface ProductService {

    ResponseEntity<?> addProduct(String sellerEmail, ProductDto productDto, Long category_id);

    ProductViewDto viewProduct(String sellerEmail, Long prodId);

    List<ProductViewDto> viewProducts(String sellerEmail);

    String removeProduct(String sellerEmail, Long productId);

    String updateProduct(String userName, Long productId, ProductUpdateDto product);

    /*admin*/
    String activateProduct(Long productId);

    String deactivateProduct(Long productId);

    ProductViewDto adminViewProduct(Long id);

    List<ProductViewDto> adminViewAllProducts();

    /*customer*/

    ProductCustomerResponseDto viewCustomerProduct(Long productId);

    List<ProductCustomerResponseDto> customerViewAllProducts(Long categoryId);

    List<ProductCustomerResponseDto> viewSimilarProducts(Long productId);
}
