package org.ttn.ecommerce.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.ttn.ecommerce.dto.AddressDto;
import org.ttn.ecommerce.dto.updateDto.ChangePasswordDto;
import org.ttn.ecommerce.dto.updateDto.UpdateSellerDto;
import org.ttn.ecommerce.entities.register.Address;
import org.ttn.ecommerce.entities.register.Seller;
import org.ttn.ecommerce.entities.register.UserEntity;
import org.ttn.ecommerce.entities.token.Token;
import org.ttn.ecommerce.repository.SellerRepository;
import org.ttn.ecommerce.repository.TokenRepository.AccessTokenRepository;
import org.ttn.ecommerce.repository.TokenRepository.AddressRepository;
import org.ttn.ecommerce.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Slf4j
public class SellerService {
    @Autowired
    SellerRepository sellerRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    AccessTokenRepository accessTokenRepository;

    @Autowired
    AddressRepository addressRepository;


    @Autowired
    private JavaMailSender javaMailSender;

    //function for allSellers API
    public List<Seller> getAllSeller(){
        return sellerRepository.findAll();
    }

    public  ResponseEntity<?> viewSellerProfile(String accessToken){

    }
    //function for update-profile API
    public ResponseEntity<String> updateSellerProfile(UpdateSellerDto updateSellerDto){
        String AccessToken=updateSellerDto.getAccessToken();
        Token token=accessTokenRepository.findByToken(AccessToken).orElseThrow(() -> new IllegalStateException("Invalid Access Token!"));

        LocalDateTime expireDateTime=token.getExpiredAt();
        if(expireDateTime.isBefore(LocalDateTime.now())){
            return new ResponseEntity<>("token is expired", HttpStatus.BAD_REQUEST);
        }
        if(userRepository.existsByEmail(token.getUserEntity().getEmail())){
            log.info("User exists with this mail");
            UserEntity user=userRepository.findByEmail(token.getUserEntity().getEmail()).get();
            if(updateSellerDto.getFirstName()!=null) {
                user.setFirstName(updateSellerDto.getFirstName());
            }

            if(updateSellerDto.getLastName()!=null) {
                user.setLastName(updateSellerDto.getLastName());
            }

            if(updateSellerDto.getMiddleName()!=null) {
                user.setMiddleName(updateSellerDto.getMiddleName());
            }

            if(updateSellerDto.getEmail()!=null) {
                user.setEmail(updateSellerDto.getEmail());
            }
            Seller seller=sellerRepository.getSellerByUserId(user.getId());
            if(updateSellerDto.getCompanyContact()!=null) {
                seller.setCompanyContact(updateSellerDto.getCompanyContact());
            }
            if(updateSellerDto.getCompanyName()!=null) {
                seller.setCompanyName(updateSellerDto.getCompanyName());
            }
            seller.setGst(updateSellerDto.getGstNumber());
            userRepository.save(user);
            sellerRepository.save(seller);
            log.info("user profile updated!");
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setSubject("Profile Updated");
            mailMessage.setText("Your profile is updated, If not done  by you contact Admin asap.\n Thanks.");
            mailMessage.setTo(user.getEmail());
            mailMessage.setFrom("rajat.gupta@tothenew.com");
            Date date = new Date();
            mailMessage.setSentDate(date);
            try {
                javaMailSender.send(mailMessage);
            } catch (MailException e) {
                log.info("Error sending mail");
            }
            return new ResponseEntity<>("Seller Your profile is updated",HttpStatus.OK);

        }else{

           return new ResponseEntity<>("Profile not updated.", HttpStatus.BAD_REQUEST);
        }

    }

    //function for update-password API
    public ResponseEntity<String> updateSellerPassword(ChangePasswordDto changePasswordDto){
        String accessToken=changePasswordDto.getAccessToken();
        Token token=accessTokenRepository.findByToken(accessToken).orElseThrow(() -> new IllegalStateException("Invalid Access Token!"));
        LocalDateTime expireDateTime = token.getExpiredAt();
        if(expireDateTime.isBefore(LocalDateTime.now())){
            return new ResponseEntity<>("Token expired",HttpStatus.BAD_REQUEST);
        }
        if(userRepository.existsByEmail(token.getUserEntity().getEmail())){
            UserEntity user=userRepository.findByEmail(token.getUserEntity().getEmail()).get();
            user.setPassword(changePasswordDto.getPassword());
            userRepository.save(user);
            log.info("Password updated!");
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setSubject("Password Updated");
            mailMessage.setText("Your password is updated, If not done  by you contact Admin asap.\n Thanks.");
            mailMessage.setTo(user.getEmail());
            mailMessage.setFrom("rajat.gupta@tothenew.com");
            Date date = new Date();
            mailMessage.setSentDate(date);
            try {
                javaMailSender.send(mailMessage);
            } catch (MailException e) {
                log.info("Error sending mail");
            }
            return new ResponseEntity<>("Password Updated",HttpStatus.OK);
        }else {
            return new ResponseEntity<>("Failed to change Password", HttpStatus.BAD_REQUEST);
        }
    }

    //function for update-address API
    public ResponseEntity<String> updateSellerAddress(Long id,AddressDto addressDto) {
        String accessToken = addressDto.getAccessToken();
        Token token = accessTokenRepository.findByToken(accessToken).orElseThrow(() -> new IllegalStateException("Invalid Access Token!"));
        LocalDateTime expireDateTime = token.getExpiredAt();
        if (expireDateTime.isBefore(LocalDateTime.now())) {
            //throw new TokenExpiredException("Access Token expired!!");
            return new ResponseEntity<>("token expired!", HttpStatus.BAD_REQUEST);
        }

        if (userRepository.existsByEmail(token.getUserEntity().getEmail())) {
            UserEntity user = userRepository.findByEmail(token.getUserEntity().getEmail()).get();
            log.info("user exists");

            if (addressRepository.existsById(id)) {
                log.info("address exists");
                Address address = addressRepository.findByid(id).get();
                address.setAddressLine(addressDto.getAddress());
                address.setLabel(addressDto.getLabel());
                address.setZipCode(addressDto.getZipcode());
                address.setCountry(addressDto.getCountry());
                address.setState(addressDto.getState());
                address.setCity(addressDto.getCity());
                log.info("trying to save the updated address");
                addressRepository.save(address);
                return new ResponseEntity<>("Address updated successfully.", HttpStatus.OK);
            } else {
                Address address = new Address();
                address.setUserEntity(user);
                address.setAddressLine(addressDto.getAddress());
                address.setCity(addressDto.getCity());
                address.setCountry(addressDto.getCountry());
                address.setState(addressDto.getState());
                address.setZipCode(addressDto.getZipcode());
                address.setLabel(addressDto.getLabel());
                addressRepository.save(address);
                log.info("Address added to the respected user");
                return new ResponseEntity<>("Added the address.", HttpStatus.CREATED);
            }
        } else {
            log.info("No address exists");
        }
    }
}
