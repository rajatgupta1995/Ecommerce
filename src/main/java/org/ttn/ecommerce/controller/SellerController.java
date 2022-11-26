package org.ttn.ecommerce.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
@RequestMapping("/api/seller")
public class SellerController {
    JWTBlackListRepository jwtBlackListRepository;
    AccessTokenRepository accessTokenRepo;
    private SellerService sellerService;

    @Autowired
    public SellerController(JWTBlackListRepository jwtBlackListRepository, AccessTokenRepository accessTokenRepo, SellerService sellerService) {
        this.jwtBlackListRepository = jwtBlackListRepository;
        this.accessTokenRepo = accessTokenRepo;
        this.sellerService = sellerService;
    }

    @GetMapping("/my-profile")
    public ResponseEntity<?> retrieveSeller(HttpServletRequest request){
        return sellerService.viewSellerProfile(request);
    }

    @GetMapping("/update-profile")
    public ResponseEntity<String> updateSellerProfile(HttpServletRequest request,@Valid @RequestBody UpdateSellerDto updateSellerDto){
        return sellerService.updateSellerProfile(request,updateSellerDto);
    }

    @GetMapping("/update-password")
    public ResponseEntity<String> updateSellerPassword(HttpServletRequest request,@Valid @RequestBody ChangePasswordDto changePasswordDto){
        return sellerService.updateSellerPassword(request,changePasswordDto);
    }

    @GetMapping("/update-address")
    public ResponseEntity<String> updateSellerAddress(HttpServletRequest request,@RequestParam("addressId") Long id,@Valid @RequestBody Address address){
        return sellerService.updateSellerAddress(request,id,address);
    }
}
