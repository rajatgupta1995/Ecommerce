package org.ttn.ecommerce.controller;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.ttn.ecommerce.dto.updateDto.AddressDto;
import org.ttn.ecommerce.dto.updateDto.ChangePasswordDto;
import org.ttn.ecommerce.dto.updateDto.UpdateCustomerDto;
import org.ttn.ecommerce.entities.register.Address;
import org.ttn.ecommerce.repository.RegisterRepository.UserRepository;
import org.ttn.ecommerce.security.SecurityConstants;
import org.ttn.ecommerce.services.CustomerService;
import org.ttn.ecommerce.services.ImageService;
import org.ttn.ecommerce.services.UserService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;

@Slf4j
@RestController
@RequestMapping(value = "/customer")
public class CustomerController {
    @Autowired
    private CustomerService customerService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private ImageService imageService;

    /**
     * API to view customer profile
     */
    @GetMapping("/my-profile")  //http://localhost:6640/customer/my-profile
    public ResponseEntity<?> viewMyProfile(Authentication authentication) {
        return customerService.viewMyProfile(authentication);
    }

    /**
     * API to add customer address
     */
    @PostMapping("/add-address")    //http://localhost:6640/customer/add-address
    public ResponseEntity<?> addNewAddress(Authentication authentication,@Valid @RequestBody Address address) {
        return customerService.addNewAddress(authentication,address);
    }

    /**
     * API to view addresses
     */
    @GetMapping("/my-addresses")    //http://localhost:6640/customer/my-addresses
    public ResponseEntity<?> viewMyAddresses(Authentication authentication){
        return customerService.viewMyAddresses(authentication);
    }

    /**
     * API to delete address by address_id
     */
    @DeleteMapping("/delete-address/{address_id}")  //http://localhost:6640/customer/delete-address/{address_id}
    public ResponseEntity<?> viewMyAddresses(@PathVariable Long address_id,Authentication authentication){
        return customerService.deleteMyAddress(address_id,authentication);
    }

    /**
     * API to update profile
     */
    @PatchMapping("/update-profile")    //http://localhost:6640/customer/update-profile
    public ResponseEntity<String> updateCustomerProfile(Authentication authentication,@Valid @RequestBody UpdateCustomerDto updateCustomerDto){
        return customerService.updateCustomerProfile(authentication,updateCustomerDto);
    }


    /**
     * API to update password
     */
    @PutMapping("/update-password") //http://localhost:6640/customer/update-password
    public ResponseEntity<String> updateCustomerPassword(Authentication authentication,@Valid @RequestBody ChangePasswordDto changePasswordDto){
        return customerService.updateCustomerPassword(authentication,changePasswordDto);
    }

    /**
     * API to update password
     */
    @PatchMapping("/update-address")    //http://localhost:6640/customer/update-address
    public ResponseEntity<String> updateSellerAddress(Authentication authentication,@RequestParam("addressId") Long id,@Valid @RequestBody AddressDto addressDto){
        return customerService.updateCustomerAddress(authentication,id,addressDto);
    }

    @PostMapping(value = "upload/image")
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

    @GetMapping("/view/image")
    public ResponseEntity<?> listFilesUsingJavaIO(HttpServletRequest request) {

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
        return imageService.getImage(email);
    }
}
