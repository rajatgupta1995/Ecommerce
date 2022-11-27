package org.ttn.ecommerce.services;

import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.stereotype.Service;
import org.ttn.ecommerce.entities.register.Customer;
import org.ttn.ecommerce.entities.register.Seller;
import org.ttn.ecommerce.entities.register.UserEntity;
import org.ttn.ecommerce.exception.NotFoundRequestException;
import org.ttn.ecommerce.repository.RegisterRepository.CustomerRepository;
import org.ttn.ecommerce.repository.RegisterRepository.SellerRepository;
import org.ttn.ecommerce.repository.RegisterRepository.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class AdminDaoService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private SellerRepository sellerRepository;

    public ResponseEntity<?> activateUser(Long user_id) {
        Optional<UserEntity> user_=userRepository.findById(user_id);
        /*checking user exists or not*/
        if(user_.isPresent()){
            log.info("User exists.");
            UserEntity user=user_.get();
            /*Checking the user is active or not*/
            if(!user.isActive()){
                user.setActive(true);
                userRepository.save(user);
                /*Send Email*/
                String toEmail= user.getEmail();
                String subject="Account Activated!!";
                String message="Your account is successfully activated by Admin.";
                emailService.sendEmail(toEmail,subject,message);
                log.info("User activated!!");
                return new ResponseEntity<>(String.format("User with this User Id: %s is activated.",user_id), HttpStatus.OK);
            }else{
                return new ResponseEntity<>("User is already activated.", HttpStatus.OK);
            }

        }else{
            log.info("No User exists!!");
            throw new NotFoundRequestException( String.format("No user exists with this user id: %s.", user_id), HttpStatus.BAD_REQUEST);
        }

    }

    public ResponseEntity<?> deactivateUser(Long user_id) {
        Optional<UserEntity> user_=userRepository.findById(user_id);
        /*checking user exists or not*/
        if(user_.isPresent()){
            UserEntity user=user_.get();
            /*Checking the user is active or not*/
            if(user.isActive()){
                user.setActive(false);
                userRepository.save(user);
                /*Send Email*/
                String toEmail= user.getEmail();
                String subject="Account Deactivated!!";
                String message="Your account has been deactivated.\\nKindly contact Admin to activate your account again, Thanks";
                emailService.sendEmail(toEmail,subject,message);
                log.info("User Deactivated!!");
                return new ResponseEntity<>(String.format("User with this User Id: %s is deactivated",user_id),HttpStatus.OK);
            }else{
                return new ResponseEntity<>("User is already deactivated.", HttpStatus.OK);
            }
        }else{
            log.info("No User exists!!");
            throw new NotFoundRequestException( String.format("No user exists with this user id: %s.", user_id), HttpStatus.BAD_REQUEST);
        }
    }

    public MappingJacksonValue listAllCustomer(String page, String size,String sortBy) {
        Pageable pageable=PageRequest.of(Integer.parseInt(page), Integer.parseInt(size), Sort.by(new Sort.Order(Sort.Direction.DESC,sortBy)));
        Page<Customer> pages = customerRepository.findAll(pageable);
        List<Customer> customers = pages.getContent();
        /*creating custom filter*/
        SimpleBeanPropertyFilter filter = SimpleBeanPropertyFilter.filterOutAllExcept("user_id", "firstName", "middleName", "lastName", "email", "isActive");

        FilterProvider filterProvider = new SimpleFilterProvider().addFilter("customerFilter", filter);

        MappingJacksonValue mappingJacksonValue=new MappingJacksonValue(customers);
        mappingJacksonValue.setFilters(filterProvider);
        return mappingJacksonValue;
    }

    public MappingJacksonValue listAllSeller(String page, String size, String sortBy) {
        Pageable pageable=PageRequest.of(Integer.parseInt(page),Integer.parseInt(size),Sort.by(new Sort.Order(Sort.Direction.DESC,sortBy)));
        Page<Seller> pages=sellerRepository.findAll(pageable);
        List<Seller> sellers=pages.getContent();

        SimpleBeanPropertyFilter filter=SimpleBeanPropertyFilter.filterOutAllExcept("user_id", "firstName", "middleName", "lastName", "email", "isActive", "companyName","addresses", "companyContact");

        FilterProvider filterProvider=new SimpleFilterProvider().addFilter("sellerFilter",filter);

        MappingJacksonValue mappingJacksonValue=new MappingJacksonValue(sellers);
        mappingJacksonValue.setFilters(filterProvider);
        return mappingJacksonValue;
    }
}
