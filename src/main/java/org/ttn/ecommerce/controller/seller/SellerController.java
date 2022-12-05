package org.ttn.ecommerce.controller.seller;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.ttn.ecommerce.dto.category.SellerCategoryResponseDTO;
import org.ttn.ecommerce.dto.updateDto.AddressDto;
import org.ttn.ecommerce.dto.updateDto.ChangePasswordDto;
import org.ttn.ecommerce.dto.updateDto.UpdateSellerDto;
import org.ttn.ecommerce.repository.tokenrepository.AccessTokenRepository;
import org.ttn.ecommerce.security.SecurityConstants;
import org.ttn.ecommerce.services.CategoryService;
import org.ttn.ecommerce.services.Impl.ImageService;
import org.ttn.ecommerce.services.SellerService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/seller")
public class SellerController {
    @Autowired
    private ImageService imageService;
    @Autowired
    private AccessTokenRepository accessTokenRepo;
    @Autowired
    private SellerService sellerService;
    @Autowired
    private CategoryService categoryService;

    /**
     * API to view seller profile
     */
    @GetMapping("/my-profile")  //http://localhost:6640/seller/my-profile
    public ResponseEntity<?> retrieveSeller(Authentication authentication){
        return sellerService.viewSellerProfile(authentication);
    }

    /**
     * API to view update seller profile
     */
    @PatchMapping("/update-profile")  //http://localhost:6640/seller/update-profile
    public ResponseEntity<String> updateSellerProfile(Authentication authentication,@Valid @RequestBody UpdateSellerDto updateSellerDto){
        return sellerService.updateSellerProfile(authentication,updateSellerDto);
    }

    /**
     * API to update seller password
     */
    @PutMapping("/update-password") //http://localhost:6640/seller/update-password
    public ResponseEntity<String> updateSellerPassword(Authentication authentication,@Valid @RequestBody ChangePasswordDto changePasswordDto){
        return sellerService.updateSellerPassword(authentication,changePasswordDto);
    }

    /**
     * API to update seller address
     */
    @PatchMapping("/update-address")  //http://localhost:6640/seller/update-address
    public ResponseEntity<String> updateSellerAddress(Authentication authentication,@Valid @RequestBody AddressDto addressDto){
        return sellerService.updateSellerAddress(authentication,addressDto);
    }

    /**
     *  API to upload image
     */
    @PostMapping(value = "upload/image")  //http://localhost:6640/seller/upload/image
    public String uploadImage(@RequestParam("image") MultipartFile image, HttpServletRequest request)
            throws IOException {
        String email = "";

        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer")) {
            String token = bearerToken.substring(7);

            Claims claims = Jwts.parser()
                    .setSigningKey(SecurityConstants.JWT_SECRET)
                    .parseClaimsJws(token)
                    .getBody();
            email = claims.getSubject();
        }
        return imageService.uploadImage(email, image);
    }

    /**
     *  API to view category for a Seller
     */
    @GetMapping("/category") //http://localhost:6640/seller/category
    public ResponseEntity<List<SellerCategoryResponseDTO>> viewSellerCategory(){
        List<SellerCategoryResponseDTO> responseList = categoryService.viewSellerCategory();
        return new ResponseEntity<>(responseList, HttpStatus.OK);
    }
}
