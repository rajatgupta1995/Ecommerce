package org.ttn.ecommerce.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.ttn.ecommerce.dto.AddressDto;
import org.ttn.ecommerce.dto.updateDto.ChangePasswordDto;
import org.ttn.ecommerce.dto.updateDto.UpdateSellerDto;
import org.ttn.ecommerce.entities.register.Seller;
import org.ttn.ecommerce.entities.token.BlackListToken;
import org.ttn.ecommerce.entities.token.Token;
import org.ttn.ecommerce.repository.TokenRepository.AccessTokenRepository;
import org.ttn.ecommerce.repository.TokenRepository.JWTBlackListRepository;
import org.ttn.ecommerce.services.SellerService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

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

    @PreAuthorize("hasRole('ROLE_SELLER')")
    @GetMapping("login")
    public String getData(){
        return "a";
    }

    @GetMapping("/my-profile")
    public ResponseEntity<?> retrieveSeller(HttpServletRequest request){
        return sellerService.viewSellerProfile(request);
    }

    @GetMapping("/allSellers")
    public List<Seller> retrieveSellers(){
        return sellerService.getAllSeller();
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
    public ResponseEntity<String> updateSellerAddress(HttpServletRequest request,@RequestParam("addressId") Long id,@Valid @RequestBody AddressDto addressDto){
        return sellerService.updateSellerAddress(request,id,addressDto);
    }

    @PostMapping("logout")
    @Transactional
    public String logout(HttpServletRequest request){
        String bearerToken = request.getHeader("Authorization");
        if(StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            String tokenValue = bearerToken.substring(7, bearerToken.length());
            //System.out.println(tokenValue);
            BlackListToken jwtBlacklist = new BlackListToken();
            Token token=accessTokenRepo.findByToken(tokenValue).get();
            jwtBlacklist.setToken(token.getToken());
            jwtBlacklist.setUserEntity(token.getUserEntity());
            jwtBlackListRepository.save(jwtBlacklist);
            accessTokenRepo.delete(token);
        }
        return "Logged out successfully";
    }
}
