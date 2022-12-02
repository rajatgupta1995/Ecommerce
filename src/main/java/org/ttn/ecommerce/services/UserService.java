package org.ttn.ecommerce.services;

import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.ttn.ecommerce.dto.LoginDto;
import org.ttn.ecommerce.dto.register.CustomerRegisterDto;
import org.ttn.ecommerce.dto.register.SellerRegisterDto;
import org.ttn.ecommerce.entities.register.UserEntity;

import javax.servlet.http.HttpServletRequest;

public interface UserService {
    ResponseEntity<String> registerCustomer(CustomerRegisterDto registerDto);

    ResponseEntity<String> registerSeller(SellerRegisterDto sellerRegisterDto);

    ResponseEntity<?> login(LoginDto loginDto, UserEntity user);

    ResponseEntity<String> activateAccount(UserEntity userEntity, String token);

    String resendActivationToken(String email);

    @Transactional
    ResponseEntity<?> logout(HttpServletRequest request);
}
