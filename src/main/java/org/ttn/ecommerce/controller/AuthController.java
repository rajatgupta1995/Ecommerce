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
import org.ttn.ecommerce.entity.register.UserEntity;
import org.ttn.ecommerce.exception.UserNotFoundException;
import org.ttn.ecommerce.repository.registerrepository.UserRepository;
import org.ttn.ecommerce.services.PasswordService;
import org.ttn.ecommerce.services.TokenService;
import org.ttn.ecommerce.services.UserService;

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
    private UserService userService;
    @Autowired
    private PasswordService passwordService;
    @Autowired
    private TokenService tokenService;

    /**
     * API to register customer
     */
    @PostMapping("customer/register") //http://localhost:6640/api/auth/customer/register
    public ResponseEntity<String> registerCustomer(@Valid @RequestBody CustomerRegisterDto customerRegisterDto){
        return userService.registerCustomer(customerRegisterDto);
    }

    /**
     * API to register seller
     */
    @PostMapping("seller/register")    //http://localhost:6640/api/auth/seller/register
    public ResponseEntity<String> registerSeller(@Valid @RequestBody SellerRegisterDto sellerRegisterDto){
        return userService.registerSeller(sellerRegisterDto);
    }

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
        if(user.isLocked()){
            log.info("Account is Locked");
            return new ResponseEntity<>("Account is locked ! Please contact admin to unlock it", HttpStatus.BAD_REQUEST);
        }
        return userService.login(loginDto,user);
    }

    /**
     * API to activate customer
     */
    @PutMapping("activate_account/{email}/{token}")  //http://localhost:6640/api/auth/activate_account/{email}/{token}
    public ResponseEntity<String> activateAccount(@PathVariable String email,@PathVariable String token){
        Optional<UserEntity> userEntity = userRepository.findByEmail(email);
        System.out.println(email);
        if(userEntity.isPresent()){
            log.info("user exists.");
            return userService.activateAccount(userEntity.get(),token);
        }
        throw new UserNotFoundException("User with this email : " + email + "does not exist." );
    }

    /**
     * @Task API to re-send activation link
     */
    @PostMapping(path = "/resendActivationToken/{email}")    //http://localhost:6640/api/auth/resendActivationToken
    public String resendActivationToken(@PathVariable String email) {
        return userService.resendActivationToken(email);
    }

    /**
     * API for forgot password
     */
    @PostMapping("forget-password/{email}")   //http://localhost:6640/api/auth/forget-password/{email}
    public ResponseEntity<?> forgetUserPassword(@Valid @PathVariable String email){
        return passwordService.forgetPassword(email);
    }

    /**
     *
     * API to reset user password
     */
    @PatchMapping("reset-password") //http://localhost:6640/api/auth/reset-password
    public ResponseEntity<String> resetPassword(@Valid @RequestBody ResetPasswordDto resetPasswordDto){
        return passwordService.resetUserPassword(resetPasswordDto);
    }

    /**
     * @Param request
     * @Task API to logout user
     */
    @PostMapping("logout")  //http://localhost:6640/api/auth/logout
    public ResponseEntity<?> logoutUser(HttpServletRequest request){
        return userService.logout(request);
    }

    /**
     * API to generate accessToken by refreshToken
     */
    @GetMapping("generate/newAccessToken")  //http://localhost:6640/api/auth/generate/newAccessToken
    public ResponseEntity<?> accessTokenFromRefreshToken(@RequestParam("userRefreshToken") String refreshToken){

        return  tokenService.newAccessToken(refreshToken);

    }

}
