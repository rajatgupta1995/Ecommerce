package org.ttn.ecommerce.controller.product;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.ttn.ecommerce.dto.product.ProductViewDto;
import org.ttn.ecommerce.services.ProductService;

import java.util.List;

@RestController
@RequestMapping("/admin")
public class ProductAdminController {
    @Autowired
    private ProductService productService;

    /**
     * API to activate Product
     * @param productId
     * @Task Activate product if it exists and in inactive state
     */
    @PatchMapping("/activateProduct")
    public String productActivation(@RequestParam Long productId) {

        return productService.activateProduct(productId);
    }

    /**
     * API to deactivate Product
     * @param productId
     * @Task DeActivate product if it exists and in active state
     */
    @PatchMapping("/deactivateProduct")
    public String productDeactivation(@RequestParam Long productId) {
        return productService.deactivateProduct(productId);
    }

    /**
     * API to view Product
     * @Task View product of particular productId
     */
    @GetMapping("/product")
    public ResponseEntity<ProductViewDto> viewAdminProduct(@RequestParam Long productId){
        ProductViewDto response = productService.adminViewProduct(productId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * API to view Product
     * @Task View all products
     */
    @GetMapping("/products")
    public ResponseEntity<List<ProductViewDto>> viewAdminProducts(){
        List<ProductViewDto> response = productService.adminViewAllProducts();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
