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
import org.springframework.transaction.annotation.Transactional;
import org.ttn.ecommerce.dto.CustomerDto;
import org.ttn.ecommerce.dto.updateDto.AddressDto;
import org.ttn.ecommerce.dto.updateDto.UpdateCustomerDto;
import org.ttn.ecommerce.dto.updateDto.ChangePasswordDto;
import org.ttn.ecommerce.entities.register.Address;
import org.ttn.ecommerce.entities.register.Customer;
import org.ttn.ecommerce.repository.RegisterRepository.CustomerRepository;
import org.ttn.ecommerce.repository.TokenRepository.AccessTokenRepository;
import org.ttn.ecommerce.repository.TokenRepository.ActivationTokenRepository;
import org.ttn.ecommerce.repository.TokenRepository.AddressRepository;
import org.ttn.ecommerce.services.CustomerService;
import org.ttn.ecommerce.services.EmailService;
import org.ttn.ecommerce.services.TokenService;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
public class CustomerServiceImpl implements CustomerService {
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
    @Override
    public ResponseEntity<?> viewMyProfile(Authentication authentication)  {
        String email= authentication.getName();
        if(customerRepository.existsByEmail(email)){
            log.info("Customer exists!");
            Customer customer = customerRepository.findByEmail(email).orElseThrow(() -> new IllegalStateException("User not found"));
            List<Address> addressList = addressRepository.findByUserId(customer.getId());
            log.info("Returning a list of Address.");
            CustomerDto customerDto=new CustomerDto();
            customerDto.setId(customer.getId());
            customerDto.setFirstName(customer.getFirstName());
            customerDto.setLastName(customer.getLastName());
            customerDto.setMiddleName(customer.getMiddleName());
            customerDto.setContact(customer.getContact());
            customerDto.setEmail(customer.getEmail());
            customerDto.setActive(customerDto.isActive());
            List<AddressDto> addressDtoList=new ArrayList<>();
            for(Address address: addressList){
                AddressDto addressDto=new AddressDto();
                addressDto.setAddress_id(address.getId());
                addressDto.setAddressLine(address.getAddressLine());
                addressDto.setCity(address.getCity());
                addressDto.setState(addressDto.getState());
                addressDto.setCountry(addressDto.getCountry());
                addressDto.setLabel(address.getLabel());
                addressDto.setZipCode(address.getZipCode());
                addressDtoList.add(addressDto);
            }

            customerDto.setAddressDto(addressDtoList);
            return new ResponseEntity<>(customerDto,HttpStatus.OK);
//            if(list.size() > 0)
//                return new ResponseEntity<>("Customer User Id: "+customer.getId()+"\nCustomer First name: "+customer.getFirstName()+"\nCustomer Last name: "+customer.getLastName()+"\nCustomer active status: "+customer.isActive()+"\nCustomer Contact: "+customer.getContact()+"\nSeller Address: \n"+list.get(0).toString(), HttpStatus.OK);
//            else
//                return new ResponseEntity<>("Customer User Id: "+customer.getId()+"\nCustomer First name: "+customer.getFirstName()+"\nCustomer Last name: "+customer.getLastName()+"\nCustomer active status: "+customer.isActive()+"\nCustomer Contact: "+customer.getContact(), HttpStatus.OK);
        }else{
            log.info("Couldn't find address related to this Customer!");
            return new ResponseEntity<>("Error fetching addresses", HttpStatus.NOT_FOUND);
        }
    }

    //add-address
    @Override
    public ResponseEntity<?> addNewAddress(Authentication authentication, Address address){
        String email= authentication.getName();

        if(customerRepository.existsByEmail(email)){
            log.info("Customer exists!");
            Set<Address> addressList = new HashSet<>();
            Customer customer=customerRepository.findByEmail(email).get();
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
    @Override
    public ResponseEntity<?> viewMyAddresses(Authentication authentication){
        String email= authentication.getName();
        if (customerRepository.existsByEmail(email)) {
            Customer customer = customerRepository.findByEmail(email).orElseThrow(() -> new IllegalStateException("No customer with this mail!"));
            List<Address> list = addressRepository.findByUserId(customer.getId());
            List<String> list1 = list.stream().map(e->{
                return e.toString();
            }).collect(Collectors.toList());
            return new ResponseEntity<>(list, HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Error fetching addresses", HttpStatus.NOT_FOUND);
        }
    }

    //delete-address
    @Override
    @Transactional
    public ResponseEntity<?> deleteMyAddress(Long id, Authentication authentication){
        String email= authentication.getName();

        if (customerRepository.existsByEmail(email)) {
            log.info("Address exists.");
            //addressRepository.deleteById(id);
            addressRepository.deleteByAddressId(id);
            log.info("deletion successful");
            return new ResponseEntity<>("Deleted Address Successfully.", HttpStatus.OK);
        }else {
            log.info("deletion failed!");
            return new ResponseEntity<>(String.format("No address found with associating address id: ", id), HttpStatus.BAD_REQUEST);
        }
    }

    //update-profile
    @Override
    public ResponseEntity<String> updateCustomerProfile(Authentication authentication, UpdateCustomerDto updateCustomerDto){
        String email= authentication.getName();
        if(customerRepository.existsByEmail(email)){
            log.info("User exists with this mail");
            Customer customer=customerRepository.findByEmail(email).orElseThrow(() -> new IllegalStateException("Invalid User!"));
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
    @Override
    public ResponseEntity<String> updateCustomerPassword(Authentication authentication, ChangePasswordDto changePasswordDto){
        String email= authentication.getName();
        if(customerRepository.existsByEmail(email)){

            Customer customer=customerRepository.findByEmail(email).orElseThrow(() -> new IllegalStateException("Invalid User"));
            String oldPassword = customer.getPassword();
            String newPassword = passwordEncoder.encode(changePasswordDto.getPassword());
            if(!changePasswordDto.getPassword().equals(changePasswordDto.getConfirmPassword()))  {
                return new ResponseEntity<>("Password and confirm Password do not match",HttpStatus.BAD_REQUEST);
            }
//            if (oldPassword.equals(newPassword)) {
//                return new ResponseEntity<>("Old password and new Password are same",HttpStatus.BAD_REQUEST);
//            }
//            if(accessTokenRepository.existsByUserId(token.getUserEntity().getId()) >0){
//                accessTokenRepository.deleteByUserId(token.getUserEntity().getId());
//            }
            customer.setPassword(newPassword);
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



    @Override
    public ResponseEntity<String> updateCustomerAddress(Authentication authentication, Long id, AddressDto addressDto) {
        String email= authentication.getName();
        if (customerRepository.existsByEmail(email)) {
            log.info("User exists");
            Customer customer = customerRepository.findByEmail(email).orElseThrow(() -> new IllegalStateException("Invalid User!"));
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
                if(addressDto.getCity()!=null)
                    address.setCity(addressDto.getCity());
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
