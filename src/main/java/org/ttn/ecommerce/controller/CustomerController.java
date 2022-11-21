package org.ttn.ecommerce.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import org.ttn.ecommerce.entities.register.Address;
import org.ttn.ecommerce.repository.CustomerRepository;
import org.ttn.ecommerce.repository.RoleRepository;
import org.ttn.ecommerce.repository.TokenRepository.AccessTokenRepository;
import org.ttn.ecommerce.repository.UserRepository;
import org.ttn.ecommerce.security.JWTGenerator;
import org.ttn.ecommerce.services.CustomerService;
import org.ttn.ecommerce.services.EmailService;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping(value = "/customer")
public class CustomerController {
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private PasswordEncoder passwordEncode;
    @Autowired
    private JWTGenerator jwtGenerator;

    @Autowired
    private EmailService emailService;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private AccessTokenRepository accessTokenRepository;

    @Autowired
    private CustomerService customerService;

    @PreAuthorize("hasRole('USER')")
    @GetMapping("login")
    public String display(){
        return "a";
    }



    @GetMapping("/my-profile")
    public ResponseEntity<?> viewMyProfile(HttpServletRequest request) {
        String accessToken = getJWTFromRequest(request);

       return customerService.viewMyProfile(accessToken);
    }

    private String getJWTFromRequest(HttpServletRequest request){
        String bearerToken = request.getHeader("Authorization");
        if(StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer")){
            return bearerToken.substring(7,bearerToken.length());

        }
        return null;
    }


    @GetMapping("/my-addresses")
    public ResponseEntity<?> viewMyAddresses(String accessToken) {
        return customerService.viewMyAddresses(accessToken);
    }

    @PostMapping("/{userId}/create-address")
    public String createAddress(@PathVariable("userId") long userId, @RequestBody Address address){
        return customerService.createAddress(userId, address);
    }


}
