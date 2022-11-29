package org.ttn.ecommerce.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import org.ttn.ecommerce.dto.updateDto.ChangePasswordDto;
import org.ttn.ecommerce.dto.updateDto.UpdateCustomerDto;
import org.ttn.ecommerce.entities.register.Address;
import org.ttn.ecommerce.entities.register.UserEntity;
import org.ttn.ecommerce.exception.UserNotFoundException;
import org.ttn.ecommerce.repository.RegisterRepository.UserRepository;
import org.ttn.ecommerce.services.CustomerService;
import org.ttn.ecommerce.services.UserDaoService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping(value = "/customer")
public class CustomerController {

    private CustomerService customerService;
    private UserRepository userRepository;
    private UserDaoService userDaoService;

    @Autowired
    public CustomerController(CustomerService customerService, UserRepository userRepository, UserDaoService userDaoService) {
        this.customerService = customerService;
        this.userRepository = userRepository;
        this.userDaoService = userDaoService;
    }

    /**
     * API to view customer profile
     */
    @GetMapping("/my-profile")  //http://localhost:6640/customer/my-profile
    public ResponseEntity<?> viewMyProfile(HttpServletRequest request) {
        return customerService.viewMyProfile(request);
    }

    /**
     * API to add customer address
     */
    @PostMapping("/add-address")    //http://localhost:6640/customer/add-address
    public ResponseEntity<?> addNewAddress(HttpServletRequest request,@Valid @RequestBody Address address) {
        return customerService.addNewAddress(request,address);
    }

    /**
     * API to view addresses
     */
    @GetMapping("/my-addresses")    //http://localhost:6640/customer/my-addresses
    public ResponseEntity<?> viewMyAddresses(HttpServletRequest request){
        return customerService.viewMyAddresses(request);
    }

    /**
     * API to delete address by address_id
     */

    @DeleteMapping("/delete-address/{address_id}")  //http://localhost:6640/customer/delete-address/{address_id}
    public ResponseEntity<?> viewMyAddresses(@PathVariable Long address_id,HttpServletRequest request){
        return customerService.deleteMyAddress(address_id,request);
    }

    /**
     * API to update profile
     */
    @PatchMapping("/update-profile")    //http://localhost:6640/customer/update-profile
    public ResponseEntity<String> updateCustomerProfile(HttpServletRequest request,@Valid @RequestBody UpdateCustomerDto updateCustomerDto){
        return customerService.updateCustomerProfile(request,updateCustomerDto);
    }


    /**
     * API to update password
     */
    @PutMapping("/update-password") //http://localhost:6640/customer/update-password
    public ResponseEntity<String> updateCustomerPassword(HttpServletRequest request,@Valid @RequestBody ChangePasswordDto changePasswordDto){
        return customerService.updateCustomerPassword(request,changePasswordDto);
    }

    /**
     * API to update password
     */
    @PatchMapping("/update-address")    //http://localhost:6640/customer/update-address
    public ResponseEntity<String> updateSellerAddress(HttpServletRequest request,@RequestParam("addressId") Long id,@Valid @RequestBody Address address){
        return customerService.updateCustomerAddress(request,id,address);
    }
}
