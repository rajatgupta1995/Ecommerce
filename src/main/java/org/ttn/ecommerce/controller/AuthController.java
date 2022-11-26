package org.ttn.ecommerce.controller;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.ttn.ecommerce.dto.LoginDto;
import org.ttn.ecommerce.dto.accountAuthService.ResetPasswordDto;
import org.ttn.ecommerce.dto.register.CustomerRegisterDto;
import org.ttn.ecommerce.dto.register.SellerRegisterDto;
import org.ttn.ecommerce.entities.register.UserEntity;
import org.ttn.ecommerce.repository.RegisterRepository.UserRepository;
import org.ttn.ecommerce.services.UserDaoService;
import org.ttn.ecommerce.services.PasswordService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Optional;

@RestController
@RequestMapping(value = "/api/auth")
@Slf4j
public class AuthController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserDaoService userDaoService;
    @Autowired
    private PasswordService userPasswordService;

    /**
     * API to login user
     */
    @PostMapping("login")  //http://localhost:6640/api/auth/login
    public ResponseEntity<?> login(@Valid @RequestBody LoginDto loginDto ){
        UserEntity user = userRepository.findByEmail(loginDto.getEmail()).get();
        if(!user.isActive()){
            log.info("Account is not active");
            return new ResponseEntity<>("Account is not active ! Please contact admin to activate it", HttpStatus.BAD_REQUEST);
        }
        return userDaoService.login(loginDto,user);
    }

    /**
     * API to register customer
     */
    @PostMapping("customer/register") //http://localhost:6640/api/auth/customer/register
    public ResponseEntity<String> registerCustomer(@Valid @RequestBody CustomerRegisterDto customerRegisterDto){
        return userDaoService.registerCustomer(customerRegisterDto);
    }

    @PostMapping("seller/register")
    public ResponseEntity<String> registerSeller(@Valid @RequestBody SellerRegisterDto sellerRegisterDto){
        return userDaoService.registerSeller(sellerRegisterDto);
    }


    /**
     * API to activate user
     */
    @PutMapping("activate_account/{email}/{token}")  //http://localhost:6640/api/auth/activate_account/{email}/{token}
    public ResponseEntity<String> activateAccount(@PathVariable String email,@PathVariable String token){

        Optional<UserEntity> userEntity = userRepository.findByEmail(email);
        if(userEntity.isPresent()){
            log.info("user exists.");
            return userDaoService.activateAccount(userEntity.get(),token);
        }
        return new ResponseEntity<>("Account with this email do not exists",HttpStatus.BAD_REQUEST);
    }

    /**
     * API for forgot password
     */
    @PostMapping("forget-password/{email}")   //http://localhost:6640/api/auth/forget-password/{email}
    public ResponseEntity<?> forgetUserPassword(@PathVariable("email") String email){
        return userPasswordService.forgetPassword(email);
    }

    /**
     * API to reset password
     */
    @PatchMapping("reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody ResetPasswordDto resetPasswordDto){
        return userPasswordService.resetUserPassword(resetPasswordDto);
    }

    @PostMapping("logout")
    public ResponseEntity<?> logoutUser(HttpServletRequest request){
        return userDaoService.logout(request);
    }

}
