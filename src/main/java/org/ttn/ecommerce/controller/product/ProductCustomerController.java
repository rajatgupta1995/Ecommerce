package org.ttn.ecommerce.controller.product;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.ttn.ecommerce.dto.product.ProductCustomerResponseDto;
import org.ttn.ecommerce.entity.product.Product;
import org.ttn.ecommerce.services.ProductService;

import java.util.List;

@RestController
@RequestMapping("/customer")
public class ProductCustomerController {
    @Autowired
    private ProductService productService;

    /**
     * API to view customer product
     * @param productId
     */
    @GetMapping("/customer/{productId}")
    public ResponseEntity<ProductCustomerResponseDto> viewCustomerProduct(@PathVariable Long productId){
        ProductCustomerResponseDto response = productService.viewCustomerProduct(productId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * API to view all product of particular category
     * @param categoryId
     */
    @GetMapping("/customer/all/{categoryId}")
    public ResponseEntity<List<ProductCustomerResponseDto>> viewAllCustomerProducts(@PathVariable Long categoryId){
        List<ProductCustomerResponseDto> response = productService.customerViewAllProducts(categoryId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * API to view similar product
     * @param productId
     */
    @GetMapping("/similar/products/{productId}")
    public ResponseEntity<List<ProductCustomerResponseDto>> viewSimilarProducts(@PathVariable Long productId){
        List<ProductCustomerResponseDto> response = productService.viewSimilarProducts(productId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
