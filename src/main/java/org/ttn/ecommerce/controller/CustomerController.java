package org.ttn.ecommerce.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import org.ttn.ecommerce.dto.updateDto.ChangePasswordDto;
import org.ttn.ecommerce.dto.updateDto.UpdateCustomerDto;
import org.ttn.ecommerce.entities.register.Address;
import org.ttn.ecommerce.services.CustomerService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
@RequestMapping(value = "/customer")
public class CustomerController {
    @Autowired
    private CustomerService customerService;

    @PreAuthorize("hasRole('USER')")
    @GetMapping("login")
    public String display(){
        return "a";
    }

    @GetMapping("/my-profile")
    public ResponseEntity<?> viewMyProfile(HttpServletRequest request) {
        return customerService.viewMyProfile(request);
    }

    @PostMapping("/add-address")
    public ResponseEntity<?> addNewAddress(HttpServletRequest request,@Valid @RequestBody Address address) {
        return customerService.addNewAddress(request,address);
    }

    @GetMapping("/my-addresses")
    public ResponseEntity<?> viewMyAddresses(HttpServletRequest request){
        return customerService.viewMyAddresses(request);
    }

    @DeleteMapping("/delete-address/{address_id}")
    public ResponseEntity<?> viewMyAddresses(@PathVariable Long address_id,HttpServletRequest request){
        return customerService.deleteMyAddress(address_id,request);
    }

    @PatchMapping("/update-profile")
    public ResponseEntity<String> updateCustomerProfile(HttpServletRequest request,@Valid @RequestBody UpdateCustomerDto updateCustomerDto){
        return customerService.updateCustomerProfile(request,updateCustomerDto);
    }

    @PutMapping("/update-password")
    public ResponseEntity<String> updateCustomerPassword(HttpServletRequest request,@Valid @RequestBody ChangePasswordDto changePasswordDto){
        return customerService.updateCustomerPassword(request,changePasswordDto);
    }

    @PatchMapping("/update-address")
    public ResponseEntity<String> updateSellerAddress(HttpServletRequest request,@RequestParam("addressId") Long id,@Valid @RequestBody Address address){
        return customerService.updateCustomerAddress(request,id,address);
    }
}
