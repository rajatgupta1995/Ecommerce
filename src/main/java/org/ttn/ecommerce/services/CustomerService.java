package org.ttn.ecommerce.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.ttn.ecommerce.dto.updateDto.UpdateCustomerDto;
import org.ttn.ecommerce.dto.updateDto.ChangePasswordDto;
import org.ttn.ecommerce.entities.register.Address;
import org.ttn.ecommerce.entities.register.Customer;
import org.ttn.ecommerce.entities.register.UserEntity;
import org.ttn.ecommerce.entities.token.ActivateUserToken;
import org.ttn.ecommerce.entities.token.Token;
import org.ttn.ecommerce.exception.UserNotFoundException;
import org.ttn.ecommerce.repository.RegisterRepository.CustomerRepository;
import org.ttn.ecommerce.repository.TokenRepository.AccessTokenRepository;
import org.ttn.ecommerce.repository.TokenRepository.ActivationTokenRepository;
import org.ttn.ecommerce.repository.TokenRepository.AddressRepository;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.*;

@Service
@Transactional
@Slf4j
public class CustomerService {
    @Autowired
    private AccessTokenRepository accessTokenRepository;
    @Autowired
    AddressRepository addressRepository;
    @Autowired
    CustomerRepository customerRepository;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    TokenService tokenService;
    @Autowired
    private JavaMailSender javaMailSender;
    @Autowired
    private ActivationTokenRepository activationTokenRepository;
    @Autowired
    private EmailService emailService;

    //view-profile
    public ResponseEntity<?> viewMyProfile(HttpServletRequest request)  {
        String accessToken = tokenService.getJWTFromRequest(request);
        Token token=accessTokenRepository.findByToken(accessToken).orElseThrow(() -> new IllegalStateException("Invalid Access Token!"));
        LocalDateTime expireDateTime=token.getExpiredAt();
        if(expireDateTime.isBefore(LocalDateTime.now())){
            log.info("Token expired!");
            return new ResponseEntity<>("Token expired!",HttpStatus.BAD_REQUEST);
        }

        if(customerRepository.existsByEmail(token.getUserEntity().getEmail())){
            log.info("Customer exists!");
            Customer customer = customerRepository.findByEmail(token.getUserEntity().getEmail()).orElseThrow(() -> new IllegalStateException("User not found"));
            List<Address> list = addressRepository.findByUserId(customer.getId());
            log.info("Returning a list of Address.");
            if(list.size() > 0)
                return new ResponseEntity<>("Customer User Id: "+customer.getId()+"\nCustomer First name: "+customer.getFirstName()+"\nCustomer Last name: "+customer.getLastName()+"\nCustomer active status: "+customer.isActive()+"\nCustomer Contact: "+customer.getContact()+"\nSeller Address: \n"+list.get(0).toString(), HttpStatus.OK);
            else
                return new ResponseEntity<>("Customer User Id: "+customer.getId()+"\nCustomer First name: "+customer.getFirstName()+"\nCustomer Last name: "+customer.getLastName()+"\nCustomer active status: "+customer.isActive()+"\nCustomer Contact: "+customer.getContact(), HttpStatus.OK);
        }else{
            log.info("Couldn't find address related to this Customer!");
            return new ResponseEntity<>("Error fetching addresses", HttpStatus.NOT_FOUND);
        }
    }

    //add-address
    public ResponseEntity<?> addNewAddress(HttpServletRequest request,Address address){
        String accessToken=tokenService.getJWTFromRequest(request);
        Token token=accessTokenRepository.findByToken(accessToken).orElseThrow(()->new IllegalStateException("Invalid Access Token!"));

        LocalDateTime expireDateTime=token.getExpiredAt();
        if(expireDateTime.isBefore(LocalDateTime.now())){
            log.info("Token expired!");
            return new ResponseEntity<>("Token Expired!",HttpStatus.BAD_REQUEST);
        }

        if(customerRepository.existsByEmail(token.getUserEntity().getEmail())){
            log.info("Customer exists!");
            Set<Address> addressList = new HashSet<>();
            Customer customer=customerRepository.findByEmail(token.getUserEntity().getEmail()).get();
            address.setUserEntity(customer);
            addressList.add(address);
            addressRepository.save(address);

            customer.setAddresses(addressList);
            log.info("New Address added for this customer");
            return new ResponseEntity<>("Address added successfully", HttpStatus.NOT_FOUND);
        }else{
            log.info("Couldn't find Customer with this UserId!");
            return new ResponseEntity<>("Error Adding addresses", HttpStatus.NOT_FOUND);
        }
    }

    //my-addresses
    public ResponseEntity<?> viewMyAddresses(HttpServletRequest request){
        String accessToken=tokenService.getJWTFromRequest(request);
       Token token =  accessTokenRepository.findByToken(accessToken).orElseThrow(() -> new IllegalStateException("Invalid Access Token!"));

        LocalDateTime expiredDateTime = token.getExpiredAt();
        if (expiredDateTime.isBefore(LocalDateTime.now())) {
            log.info("Token expired!");
            return new ResponseEntity<>("Token Expired!",HttpStatus.BAD_REQUEST);
        }
        if (customerRepository.existsByEmail(token.getUserEntity().getEmail())) {
            Customer customer = customerRepository.findByEmail(token.getUserEntity().getEmail()).get();
            List<Address> list = addressRepository.findByUserId(customer.getId());
            return new ResponseEntity<>(list, HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Error fetching addresses", HttpStatus.NOT_FOUND);
        }
    }

    //delete-address
    public ResponseEntity<?> deleteMyAddress(Long id,HttpServletRequest request){
        String accessToken=tokenService.getJWTFromRequest(request);
        Token token =  accessTokenRepository.findByToken(accessToken).orElseThrow(() -> new IllegalStateException("Invalid Access Token!"));

        LocalDateTime expiredDateTime = token.getExpiredAt();
        if (expiredDateTime.isBefore(LocalDateTime.now())) {
            log.info("Token expired!");
            return new ResponseEntity<>("Token Expired!",HttpStatus.BAD_REQUEST);
        }

        if (customerRepository.existsByEmail(token.getUserEntity().getEmail())) {
            log.info("Address exists.");
            addressRepository.deleteById(id);
            log.info("deletion successful");
            return new ResponseEntity<>("Deleted Address Successfully.", HttpStatus.OK);
        }else {
            log.info("deletion failed!");
            return new ResponseEntity<>(String.format("No address found with associating address id: ", id), HttpStatus.BAD_REQUEST);
        }
    }

    //update-profile
    public ResponseEntity<String> updateCustomerProfile(HttpServletRequest request, UpdateCustomerDto updateCustomerDto){
        String accessToken = tokenService.getJWTFromRequest(request);
        Token token=accessTokenRepository.findByToken(accessToken).orElseThrow(() -> new IllegalStateException("Invalid Access Token!"));

        LocalDateTime expireDateTime=token.getExpiredAt();
        if(expireDateTime.isBefore(LocalDateTime.now())){
            return new ResponseEntity<>("token is expired", HttpStatus.BAD_REQUEST);
        }
        if(customerRepository.existsByEmail(token.getUserEntity().getEmail())){
            log.info("User exists with this mail");
            Customer customer=customerRepository.findByEmail(token.getUserEntity().getEmail()).orElseThrow(() -> new IllegalStateException("Invalid User!"));
            if(updateCustomerDto.getFirstName()!=null) {
                customer.setFirstName(updateCustomerDto.getFirstName());
            }

            if(updateCustomerDto.getLastName()!=null) {
                customer.setLastName(updateCustomerDto.getLastName());
            }

            if(updateCustomerDto.getMiddleName()!=null) {
                customer.setMiddleName(updateCustomerDto.getMiddleName());
            }

            if(updateCustomerDto.getContact()!=null) {
                customer.setContact(updateCustomerDto.getContact());
            }
            customerRepository.save(customer);
            log.info("user profile updated!");
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setSubject("Profile Updated");
            mailMessage.setText("Your profile is updated, If not done  by you contact Admin asap.\n Thanks.");
            mailMessage.setTo(customer.getEmail());
            mailMessage.setFrom("rajat.gupta@tothenew.com");
            Date date = new Date();
            mailMessage.setSentDate(date);
            try {
                javaMailSender.send(mailMessage);
            } catch (MailException e) {
                log.info("Error sending mail");
            }
            return new ResponseEntity<>("Customer your profile is updated",HttpStatus.OK);

        }else{

            return new ResponseEntity<>("Profile not updated.", HttpStatus.BAD_REQUEST);
        }
    }

    //update-password
    public ResponseEntity<String> updateCustomerPassword(HttpServletRequest request, ChangePasswordDto changePasswordDto){
        String accessToken = tokenService.getJWTFromRequest(request);
        Token token=accessTokenRepository.findByToken(accessToken).orElseThrow(() -> new IllegalStateException("Invalid Access Token!"));
        LocalDateTime expireDateTime=token.getExpiredAt();


        if(expireDateTime.isBefore(LocalDateTime.now())){
            log.info("Token expired!");
            return new ResponseEntity<>("token is expired", HttpStatus.BAD_REQUEST);
        }

        if(customerRepository.existsByEmail(token.getUserEntity().getEmail())){

            Customer customer=customerRepository.findByEmail(token.getUserEntity().getEmail()).orElseThrow(() -> new IllegalStateException("Invalid User"));
            String oldPassword = customer.getPassword();
            String newPassword = passwordEncoder.encode(changePasswordDto.getPassword());
            if(!changePasswordDto.getPassword().equals(changePasswordDto.getConfirmPassword()))  {
                return new ResponseEntity<>("Password and confirm Password do not match",HttpStatus.BAD_REQUEST);
            }
            if (oldPassword.equals(newPassword)) {
                return new ResponseEntity<>("Old password and new Password are same",HttpStatus.BAD_REQUEST);
            }
            customer.setPassword(changePasswordDto.getPassword());
            customerRepository.save(customer);
            log.info("Password updated!");
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setSubject("Password Updated");
            mailMessage.setText("Your password is updated, If not done  by you contact Admin asap.\n Thanks.");
            mailMessage.setTo(customer.getEmail());
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



    public ResponseEntity<String> updateCustomerAddress(HttpServletRequest request,Long id, Address address_) {
        String accessToken = tokenService.getJWTFromRequest(request);
        Token token = accessTokenRepository.findByToken(accessToken).orElseThrow(() -> new IllegalStateException("Invalid Access Token!"));
        LocalDateTime expireDateTime = token.getExpiredAt();
        if (expireDateTime.isBefore(LocalDateTime.now())) {
            log.info("Token expired!");
            return new ResponseEntity<>("Token expired!", HttpStatus.BAD_REQUEST);
        }

        if (customerRepository.existsByEmail(token.getUserEntity().getEmail())) {
            log.info("User exists");
            Customer customer = customerRepository.findByEmail(token.getUserEntity().getEmail()).orElseThrow(() -> new IllegalStateException("Invalid User!"));
            if (addressRepository.existsById(id)) {
                log.info("address exists");
                Address address = addressRepository.findByid(id).get(0);
                if(address_.getAddressLine()!=null)
                    address.setAddressLine(address_.getAddressLine());
                if(address_.getLabel()!=null)
                    address.setLabel(address_.getLabel());
                if(address_.getZipCode()!=null)
                    address.setZipCode(address_.getZipCode());
                if(address_.getCountry()!=null)
                    address.setCountry(address_.getCountry());
                if(address_.getState()!=null)
                    address.setState(address_.getState());
                if(address_.getCity()!=null)
                    address.setCity(address_.getCity());
                log.info("trying to save the updated address");
                addressRepository.save(address);
                return new ResponseEntity<>("Address updated successfully.", HttpStatus.OK);
            } else{
                return new ResponseEntity<>("Address Id does not exists", HttpStatus.NOT_FOUND);
            }
        } else {
            log.info("No address exists");
            return new ResponseEntity<>(String.format("No address exists with address id: "+id), HttpStatus.NOT_FOUND);
        }
    }
}
