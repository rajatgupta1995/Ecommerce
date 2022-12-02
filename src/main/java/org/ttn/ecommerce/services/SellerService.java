package org.ttn.ecommerce.services;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.ttn.ecommerce.dto.updateDto.AddressDto;
import org.ttn.ecommerce.dto.updateDto.ChangePasswordDto;
import org.ttn.ecommerce.dto.updateDto.UpdateSellerDto;
import org.ttn.ecommerce.entities.register.Seller;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface SellerService {
    //function for allSellers API
    List<Seller> getAllSeller();

    //function for sellerProfile API
    ResponseEntity<?> viewSellerProfile(Authentication authentication);

    //function for update-profile API
    ResponseEntity<String> updateSellerProfile(Authentication authentication, UpdateSellerDto updateSellerDto);

    //function for update-password API
    ResponseEntity<String> updateSellerPassword(Authentication authentication, ChangePasswordDto changePasswordDto);

    //function for update-address API
    ResponseEntity<String> updateSellerAddress(Authentication authentication, Long id, AddressDto addressDto);
}
