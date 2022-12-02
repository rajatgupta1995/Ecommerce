package org.ttn.ecommerce.services;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.ttn.ecommerce.dto.product.ProductDto;
import org.ttn.ecommerce.dto.product.ProductUpdateDto;
import org.ttn.ecommerce.dto.product.ProductViewDto;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface ProductService {

    ResponseEntity<?> addProduct(String sellerEmail, ProductDto productDto, Long category_id);

    ProductViewDto viewProduct(String sellerEmail, Long prodId);

    List<ProductViewDto> viewProducts(String sellerEmail);

    String removeProduct(String sellerEmail, Long productId);

    String updateProduct(String userName, Long productId, ProductUpdateDto product);
}
