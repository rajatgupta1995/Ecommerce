package org.ttn.ecommerce.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.ttn.ecommerce.dto.updateDto.ChangePasswordDto;
import org.ttn.ecommerce.dto.updateDto.UpdateSellerDto;
import org.ttn.ecommerce.entities.register.Address;
import org.ttn.ecommerce.repository.TokenRepository.AccessTokenRepository;
import org.ttn.ecommerce.repository.TokenRepository.JWTBlackListRepository;
import org.ttn.ecommerce.services.SellerService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
@RequestMapping("/seller")
public class SellerController {
    private JWTBlackListRepository jwtBlackListRepository;
    private AccessTokenRepository accessTokenRepo;
    private SellerService sellerService;

    @Autowired
    public SellerController(JWTBlackListRepository jwtBlackListRepository, AccessTokenRepository accessTokenRepo, SellerService sellerService) {
        this.jwtBlackListRepository = jwtBlackListRepository;
        this.accessTokenRepo = accessTokenRepo;
        this.sellerService = sellerService;
    }

    /**
     * API to view seller profile
     */
    @GetMapping("/my-profile")  //http://localhost:6640/seller/my-profile
    public ResponseEntity<?> retrieveSeller(HttpServletRequest request){
        return sellerService.viewSellerProfile(request);
    }

    /**
     * API to view update seller profile
     */
    @GetMapping("/update-profile")  //http://localhost:6640/seller/update-profile
    public ResponseEntity<String> updateSellerProfile(HttpServletRequest request,@Valid @RequestBody UpdateSellerDto updateSellerDto){
        return sellerService.updateSellerProfile(request,updateSellerDto);
    }

    /**
     * API to update seller password
     */
    @GetMapping("/update-password") //http://localhost:6640/seller/update-password
    public ResponseEntity<String> updateSellerPassword(HttpServletRequest request,@Valid @RequestBody ChangePasswordDto changePasswordDto){
        return sellerService.updateSellerPassword(request,changePasswordDto);
    }

    /**
     * API to update seller address
     */
    @GetMapping("/update-address")  //http://localhost:6640/seller/update-address
    public ResponseEntity<String> updateSellerAddress(HttpServletRequest request,@RequestParam("addressId") Long id,@Valid @RequestBody Address address){
        return sellerService.updateSellerAddress(request,id,address);
    }
}
