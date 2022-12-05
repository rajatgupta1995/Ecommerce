package org.ttn.ecommerce.services;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.ttn.ecommerce.dto.updateDto.AddressDto;
import org.ttn.ecommerce.dto.updateDto.ChangePasswordDto;
import org.ttn.ecommerce.dto.updateDto.UpdateCustomerDto;
import org.ttn.ecommerce.entity.register.Address;

public interface CustomerService {
    //view-profile
    ResponseEntity<?> viewMyProfile(Authentication authentication);

    //add-address
    ResponseEntity<?> addNewAddress(Authentication authentication, Address address);

    //my-addresses
    ResponseEntity<?> viewMyAddresses(Authentication authentication);

    //delete-address
    @Transactional
    ResponseEntity<?> deleteMyAddress(Long id, Authentication authentication);

    //update-profile
    ResponseEntity<String> updateCustomerProfile(Authentication authentication, UpdateCustomerDto updateCustomerDto);

    //update-password
    ResponseEntity<String> updateCustomerPassword(Authentication authentication, ChangePasswordDto changePasswordDto);

    ResponseEntity<String> updateCustomerAddress(Authentication authentication, Long id, AddressDto addressDto);
}
