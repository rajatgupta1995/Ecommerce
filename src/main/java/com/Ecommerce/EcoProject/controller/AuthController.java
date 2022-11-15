package com.Ecommerce.EcoProject.controller;


import com.Ecommerce.EcoProject.EmailSenderService;
import com.Ecommerce.EcoProject.Model.register.EntityUser;
import com.Ecommerce.EcoProject.Model.register.Role;
import com.Ecommerce.EcoProject.dto.AuthResponseDto;
import com.Ecommerce.EcoProject.dto.LoginDto;
import com.Ecommerce.EcoProject.dto.RegisterDto;
import com.Ecommerce.EcoProject.repository.RoleRepository;
import com.Ecommerce.EcoProject.repository.UserRepository;
import com.Ecommerce.EcoProject.security.JWTGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import java.util.Collections;

@RestController
@RequestMapping(value = "/api/auth")
public class AuthController {

    private AuthenticationManager authenticationManager;

    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private PasswordEncoder passwordEncode;

    private JWTGenerator jwtGenerator;

    @Autowired
    private EmailSenderService senderService;

    public AuthController(AuthenticationManager authenticationManager, UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncode, JWTGenerator jwtGenerator) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncode = passwordEncode;
        this.jwtGenerator = jwtGenerator;
    }

//    @EventListener(ApplicationReadyEvent.class)
    public void triggerMail(String email,String token) throws MessagingException {
        senderService.sendSimpleEmail(email,

                "This is email subject",token);

    }

    @PostMapping("login")
    public ResponseEntity<AuthResponseDto> login(@RequestBody LoginDto loginDto ){
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginDto.getEmail(),loginDto.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = jwtGenerator.generateToken(authentication);
        try {
            triggerMail(loginDto.getEmail(), token);
        }catch (Exception e){
            System.out.println("exception occured");
        }
        return new ResponseEntity<>(new AuthResponseDto(token),HttpStatus.OK);

    }

    @GetMapping("hello")
    public String display(){
        return "hello";
    }



    @PostMapping("register")
    public ResponseEntity<String> register(@RequestBody RegisterDto registerDto){
        if(userRepository.existsByemail(registerDto.getEmail())){
            return new ResponseEntity<>("Email already taken", HttpStatus.BAD_REQUEST);
        }

        EntityUser user = new EntityUser();
        user.setEmail(registerDto.getEmail());
        user.setPassword(passwordEncode.encode(registerDto.getPassword()));

        System.out.println(user);
        Role roles = roleRepository.findByAuthority("CUSTOMER").get();
        System.out.println(roles.getAuthority());
        user.setRoles(Collections.singletonList(roles));

        userRepository.save(user);

        return new ResponseEntity<>("Customer Registered Successfully",HttpStatus.OK);

    }
}
