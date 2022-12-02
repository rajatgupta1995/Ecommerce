package org.ttn.ecommerce.services.Impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.ttn.ecommerce.dto.updateDto.AddressDto;
import org.ttn.ecommerce.dto.updateDto.ChangePasswordDto;
import org.ttn.ecommerce.dto.updateDto.UpdateSellerDto;
import org.ttn.ecommerce.entities.register.Address;
import org.ttn.ecommerce.entities.register.Seller;
import org.ttn.ecommerce.entities.register.UserEntity;
import org.ttn.ecommerce.entities.token.Token;
import org.ttn.ecommerce.repository.RegisterRepository.SellerRepository;
import org.ttn.ecommerce.repository.TokenRepository.AccessTokenRepository;
import org.ttn.ecommerce.repository.TokenRepository.AddressRepository;
import org.ttn.ecommerce.repository.RegisterRepository.UserRepository;
import org.ttn.ecommerce.services.SellerService;
import org.ttn.ecommerce.services.TokenService;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
@Service
@Slf4j
public class SellerServiceImpl implements SellerService {
    @Autowired
    SellerRepository sellerRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    AccessTokenRepository accessTokenRepository;
    @Autowired
    AddressRepository addressRepository;
    @Autowired
    TokenService tokenService;
    @Autowired
    private JavaMailSender javaMailSender;
    @Autowired
    private PasswordEncoder passwordEncoder;

    //function for allSellers API
    @Override
    public List<Seller> getAllSeller(){
        return sellerRepository.findAll();
    }

    //function for sellerProfile API
    @Override
    public  ResponseEntity<?> viewSellerProfile(Authentication authentication){
        String email=authentication.getName();
        //can use userRepository or sellerRepository
        if(sellerRepository.existsByEmail(email)){
            log.info("User exists!");
            //UserEntity or Seller
            Seller user = sellerRepository.findByEmail(email).orElseThrow(() -> new IllegalStateException("User not found"));
            List<Address> list = addressRepository.findByUserId(user.getId());
            log.info("returning a list of objects.");
            if(list.size() > 0)
                //sellerRepository.getCompanyNameOfUserId(user.getId()) or user.getCompanyContact()
                return new ResponseEntity<>("Seller User Id: "+user.getId()+"\nSeller First name: "+user.getFirstName()+"\nSeller Last name: "+user.getLastName()+"\nSeller active status: "+user.isActive()+"\nContact: "+user.getCompanyContact()+"\nSeller companyName: "+user.getCompanyName()+"\nSeller gstNumber: "+user.getGst()+"\nSeller Address: \n"+list.get(0).toString(), HttpStatus.OK);
            else
                return new ResponseEntity<>("Seller User Id: "+user.getId()+"\nSeller First name: "+user.getFirstName()+"\nSeller Last name: "+user.getLastName()+"\nSeller active status: "+user.isActive()+"\nContact: "+user.getCompanyContact()+"\nSeller companyName: "+user.getCompanyName()+"\nSeller gstNumber: "+user.getGst(), HttpStatus.OK);

        }else{
            log.info("Couldn't find address related to user!!!");
            return new ResponseEntity<>("Error fetching addresses", HttpStatus.NOT_FOUND);
        }
    }
    //function for update-profile API
    @Override
    public ResponseEntity<String> updateSellerProfile(Authentication authentication, UpdateSellerDto updateSellerDto){
        String email=authentication.getName();
        if(sellerRepository.existsByEmail(email)){
            log.info("User exists with this mail");
            Seller user=sellerRepository.findByEmail(email).orElseThrow(() -> new IllegalStateException("Invalid User!"));
            if(updateSellerDto.getFirstName()!=null) {
                user.setFirstName(updateSellerDto.getFirstName());

            }
            if(updateSellerDto.getLastName()!=null) {
                user.setLastName(updateSellerDto.getLastName());
            }
            if(updateSellerDto.getMiddleName()!=null) {
                user.setMiddleName(updateSellerDto.getMiddleName());
            }
            Seller seller=sellerRepository.getSellerByUserId(user.getId());
            if(updateSellerDto.getCompanyContact()!=null) {
                seller.setCompanyContact(updateSellerDto.getCompanyContact());
            }
            if(updateSellerDto.getCompanyName()!=null) {
                seller.setCompanyName(updateSellerDto.getCompanyName());
            }
            sellerRepository.save(user);
            sellerRepository.save(seller);
            log.info("user profile updated!");
            /*send email*/
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
    @Override
    public ResponseEntity<String> updateSellerPassword(Authentication authentication, ChangePasswordDto changePasswordDto){
        String email=authentication.getName();
        if(userRepository.existsByEmail(email)){
            UserEntity user=userRepository.findByEmail(email).orElseThrow(() -> new IllegalStateException("Invalid User"));
            user.setPassword(passwordEncoder.encode(changePasswordDto.getPassword()));
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
    @Override
    public ResponseEntity<String> updateSellerAddress(Authentication authentication, Long id, AddressDto addressDto) {
        String email=authentication.getName();
        if (userRepository.existsByEmail(email)) {
            UserEntity user = userRepository.findByEmail(email).orElseThrow(() -> new IllegalStateException("Invalid User!"));
            log.info("user exists");

            if (addressRepository.existsById(id)) {
                log.info("address exists");
                Address address = addressRepository.findByid(id).get(0);
                if(addressDto.getAddressLine()!=null)
                    address.setAddressLine(addressDto.getAddressLine());
                if(addressDto.getLabel()!=null)
                    address.setLabel(addressDto.getLabel());
                if(addressDto.getZipCode()!=null)
                    address.setZipCode(addressDto.getZipCode());
                if(addressDto.getCountry()!=null)
                    address.setCountry(addressDto.getCountry());
                if(addressDto.getState()!=null)
                    address.setState(addressDto.getState());
                if(addressDto.getCountry()!=null)
                    address.setCity(addressDto.getCity());
                log.info("trying to save the updated address");
                addressRepository.save(address);
                return new ResponseEntity<>("Address updated successfully.", HttpStatus.OK);
            } else {
                Address address = new Address();
                address.setUserEntity(user);
                address.setAddressLine(addressDto.getAddressLine());
                address.setCity(addressDto.getCity());
                address.setCountry(addressDto.getCountry());
                address.setState(addressDto.getState());
                address.setZipCode(addressDto.getZipCode());
                address.setLabel(addressDto.getLabel());
                addressRepository.save(address);
                log.info("Address added to the respected user");
                return new ResponseEntity<>("Added the address.", HttpStatus.CREATED);
            }
        } else {
            log.info("No address exists");
            return new ResponseEntity<>(String.format("No address exists with address id: "+id), HttpStatus.NOT_FOUND);
        }
    }
}
