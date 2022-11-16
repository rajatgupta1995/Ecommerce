package com.Ecommerce.EcoProject.controller;


import com.Ecommerce.EcoProject.Model.register.EntityUser;
import com.Ecommerce.EcoProject.Model.register.Role;
import com.Ecommerce.EcoProject.dto.AuthResponseDto;
import com.Ecommerce.EcoProject.dto.LoginDto;
import com.Ecommerce.EcoProject.dto.RegisterDto;
import com.Ecommerce.EcoProject.repository.RoleRepository;
import com.Ecommerce.EcoProject.repository.UserRepository;
import com.Ecommerce.EcoProject.security.JWTGenerator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Collections;
import java.util.Properties;

@RestController
@RequestMapping(value = "/api/auth")
public class AuthController {

    private AuthenticationManager authenticationManager;

    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private PasswordEncoder passwordEncode;

    private JWTGenerator jwtGenerator;

    public AuthController(AuthenticationManager authenticationManager, UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncode, JWTGenerator jwtGenerator) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncode = passwordEncode;
        this.jwtGenerator = jwtGenerator;
    }

    private void sendEmail(String message, String subject, String to, String from) {

        //Variable for gmail
        String host="smtp.gmail.com";

        //get the system properties
        Properties properties = System.getProperties();
        System.out.println("PROPERTIES "+properties);

        //setting important information to properties object

        //host set
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port","465");
        properties.put("mail.smtp.ssl.enable","true");
        properties.put("mail.smtp.auth","true");

        //Step 1: to get the session object..
        Session session=Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("rajat.gupta1@tothenew.com", "xthieyrubvfdoucs");
            }



        });

        session.setDebug(true);

        //Step 2 : compose the message [text,multi media]
        MimeMessage m = new MimeMessage(session);

        try {

            //from email
          m.setFrom(from);


            //adding recipient to message
            m.addRecipient(Message.RecipientType.TO, new InternetAddress(to));

            //adding subject to message
            m.setSubject(subject);


            //adding text to message
            m.setText(message);

            //send

            //Step 3 : send the message using Transport class
            Transport.send(m);

            System.out.println("Sent success...................");


        }catch (Exception e) {
            e.printStackTrace();
        }

    }

    @PostMapping("login")
    public ResponseEntity<AuthResponseDto> login(@RequestBody LoginDto loginDto ){
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginDto.getEmail(),loginDto.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = jwtGenerator.generateToken(authentication);

        System.out.println("preparing to send message ...");
        String message = token;
        String subject = "CodersArea : Confirmation";
        String to = loginDto.getEmail();
        String from = "rajat.gupta1@tothenew.com";

    	sendEmail(message,subject,to,from);
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
