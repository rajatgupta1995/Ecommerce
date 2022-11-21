package org.ttn.ecommerce.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.ttn.ecommerce.dto.LoginDto;
import org.ttn.ecommerce.dto.accountAuthService.ResetPasswordDto;
import org.ttn.ecommerce.dto.register.CustomerRegisterDto;
import org.ttn.ecommerce.dto.register.SellerRegisterDto;
import org.ttn.ecommerce.entities.register.UserEntity;
import org.ttn.ecommerce.entities.token.BlackListToken;
import org.ttn.ecommerce.entities.token.Token;
import org.ttn.ecommerce.repository.RoleRepository;
import org.ttn.ecommerce.repository.UserRepository;
import org.ttn.ecommerce.security.JWTGenerator;
import org.ttn.ecommerce.services.UserDaoService;
import org.ttn.ecommerce.services.UserPasswordService;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

@RestController
@RequestMapping(value = "/api/auth")
public class PublicController {

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
    private UserDaoService userDaoService;
    @Autowired
    private UserPasswordService userPasswordService;

    @PostMapping("login")
    public ResponseEntity<?> login(@RequestBody LoginDto loginDto ){

        UserEntity user = userRepository.findByEmail(loginDto.getEmail()).get();

        if(!user.isActive()){
            return new ResponseEntity<>("Account is not active ! Please contact admin to activate it", HttpStatus.BAD_REQUEST);
        }
        return userDaoService.loginCustomer(loginDto,user);
    }

    @GetMapping("hello")
    public String display(){
        return "hello";
    }


    @PostMapping("customer/register")
    public ResponseEntity<String> registerCustomer(@RequestBody CustomerRegisterDto customerRegisterDto){

        return userDaoService.registerCustomer(customerRegisterDto);

    }

    @PostMapping("seller/register")
    public ResponseEntity<String> registerSeller(@RequestBody SellerRegisterDto sellerRegisterDto){

        return userDaoService.registerSeller(sellerRegisterDto);
    }

    @GetMapping("activate_account/{email}/{token}")
    public ResponseEntity<String> confirmAccount(@PathVariable("email") String email,@PathVariable("token") String token){

        Optional<UserEntity> userEntity = userRepository.findByEmail(email);
        if(userEntity.isPresent()){
            return userDaoService.confirmAccount(userEntity.get(),token);
        }

        return new ResponseEntity<>("Account with this email do not exists",HttpStatus.BAD_REQUEST);
    }

    @GetMapping("forget-password/{email}")
    public ResponseEntity<?> forgetUserPassword(@PathVariable("email") String email){
        return userPasswordService.forgetPassword(email);
    }

    @PatchMapping("reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody ResetPasswordDto resetPasswordDto){
        return userPasswordService.resetUserPassword(resetPasswordDto);
    }

    @PostMapping("customer/logout")
    public String logoutCustomer(HttpServletRequest request){
         return userPasswordService.logout(request);
    }
}
