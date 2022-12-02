package org.ttn.ecommerce.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.ttn.ecommerce.dto.product.ProductDto;
import org.ttn.ecommerce.dto.product.ProductUpdateDto;
import org.ttn.ecommerce.dto.product.ProductViewDto;
import org.ttn.ecommerce.services.ProductService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/seller")
public class ProductController {
    @Autowired
    private ProductService productService;

    /**
     * API to add product
     */
    @PostMapping("/add-product") //http://localhost:6640/seller/add-product/13
    public ResponseEntity<?> addProduct(Authentication authentication, @Valid @RequestBody ProductDto productDto, @RequestParam Long category_id){
        String userName = authentication.getName();
        return productService.addProduct(userName,productDto,category_id);
    }

    /**
     * API to view product
     */
    @GetMapping("/view/product") //http://localhost:6640/seller/view/product
    public ResponseEntity<ProductViewDto> viewProduct(Authentication authentication, @RequestParam Long productId){
        String userName = authentication.getName();
        return new ResponseEntity<>(productService.viewProduct(userName,productId), HttpStatus.OK);
    }

    /**
     * API to view products
     */
    @GetMapping("/view/products") //http://localhost:6640/seller/view/products
    public ResponseEntity<List<ProductViewDto>> viewProducts(Authentication authentication){
        String userName = authentication.getName();
        return new ResponseEntity<>(productService.viewProducts(userName), HttpStatus.OK);
    }

    /**
     * API to delete product
     */
    @DeleteMapping("/product/delete")
    public ResponseEntity<String> deleteProduct(Authentication authentication,@RequestParam Long productId){
        String userName = authentication.getName();
        return new ResponseEntity<>(productService.removeProduct(userName,productId),HttpStatus.OK);
    }

    @PatchMapping("update/product")
    public ResponseEntity<String> updateProduct(Authentication authentication, @RequestParam Long productId, @RequestBody ProductUpdateDto product){
        String userName = authentication.getName();
        return new ResponseEntity<>(productService.updateProduct(userName,productId,product),HttpStatus.OK);
    }



}
