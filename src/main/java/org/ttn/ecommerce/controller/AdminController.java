package org.ttn.ecommerce.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.web.bind.annotation.*;
import org.ttn.ecommerce.services.AdminDaoService;

@RestController
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    private AdminDaoService adminDaoService;

    /**
     * API to list all the registered customers
     */
    @GetMapping("list-customers")       //http://localhost:6640/admin/list-customers
    public MappingJacksonValue getCustomers(@RequestParam(defaultValue = "0") String page, @RequestParam(defaultValue = "10")String size,@RequestParam("sortBy")String sortBy){
        return adminDaoService.listAllCustomer(page, size,sortBy);
    }

    /**
     * API to list all the registered sellers
     */
    @GetMapping("list-sellers")       //http://localhost:6640/admin/list-sellers
    public MappingJacksonValue getSellers(@RequestParam(defaultValue = "0") String page, @RequestParam(defaultValue = "10")String size,@RequestParam("sortBy")String sortBy){
        return adminDaoService.listAllSeller(page, size,sortBy);
    }

    /**
     * API to deactivate customer
     */
    @PatchMapping("/deactivate/customer/{id}") //http://localhost:6640/deactivate/customer/{id}
    public ResponseEntity<?> deactivateCustomer(@PathVariable Long id){
        return adminDaoService.deactivateUser(id);
    }

    /**
     * API to deactivate seller
     */
    @PatchMapping("/deactivate/seller/{id}") //http://localhost:6640/deactivate/seller/{id}
    public ResponseEntity<?> deactivateSeller(@PathVariable Long id){
        return adminDaoService.deactivateUser(id);
    }

    /**
     * API to activate customer
     */
    @PatchMapping("/activate/customer/{id}") //http://localhost:6640/activate/customer/{id}
    public ResponseEntity<?> activateCustomer(@PathVariable Long id){
        return adminDaoService.activateUser(id);
    }

    /**
     * API to activate seller
     */
    @PatchMapping("/activate/seller/{id}") //http://localhost:6640/activate/seller/{id}
    public ResponseEntity<?> activateSeller(@PathVariable Long id){
        return adminDaoService.activateUser(id);
    }
}
