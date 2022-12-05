package org.ttn.ecommerce.services.Impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.ttn.ecommerce.dto.CustomerDto;
import org.ttn.ecommerce.dto.register.SellerResponseDto;
import org.ttn.ecommerce.dto.updateDto.AddressDto;
import org.ttn.ecommerce.dto.updateDto.ChangePasswordDto;
import org.ttn.ecommerce.dto.updateDto.UpdateSellerDto;
import org.ttn.ecommerce.entity.register.Address;
import org.ttn.ecommerce.entity.register.Customer;
import org.ttn.ecommerce.entity.register.Seller;
import org.ttn.ecommerce.entity.register.UserEntity;
import org.ttn.ecommerce.repository.registerrepository.SellerRepository;
import org.ttn.ecommerce.repository.registerrepository.UserRepository;
import org.ttn.ecommerce.repository.tokenrepository.AccessTokenRepository;
import org.ttn.ecommerce.repository.tokenrepository.AddressRepository;
import org.ttn.ecommerce.services.SellerService;
import org.ttn.ecommerce.services.TokenService;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

@Service
@Transactional
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
    @Autowired
    private ImageService imageService;
    @Autowired
    private MessageSource messageSource;

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
            log.info("Seller exists!");
            Seller seller = sellerRepository.findByEmail(email).orElseThrow(() -> new IllegalStateException("User not found"));
            List<Address> addressList = addressRepository.findByUserId(seller.getId());
            log.info("Returning a list of Address.");
            SellerResponseDto sellerResponseDto=new SellerResponseDto();
            sellerResponseDto.setId(seller.getId());
            sellerResponseDto.setFirstName(seller.getFirstName());
            sellerResponseDto.setLastName(seller.getLastName());
            sellerResponseDto.setMiddleName(seller.getMiddleName());
            sellerResponseDto.setContact(seller.getCompanyContact());
            sellerResponseDto.setEmail(seller.getEmail());
            sellerResponseDto.setActive(sellerResponseDto.isActive());
            sellerResponseDto.setImagePath(imageService.getImagePath(seller));


            List<AddressDto> addressDtoList=new ArrayList<>();
            for(Address address: addressList){
                AddressDto addressDto=new AddressDto();
                addressDto.setAddress_id(address.getId());
                addressDto.setAddressLine(address.getAddressLine());
                addressDto.setCity(address.getCity());
                addressDto.setState(address.getState());
                addressDto.setCountry(address.getCountry());
                addressDto.setLabel(address.getLabel());
                addressDto.setZipCode(address.getZipCode());
                addressDtoList.add(addressDto);
            }

            sellerResponseDto.setAddressDto(addressDtoList);
            return new ResponseEntity<>(sellerResponseDto,HttpStatus.OK);
        }else{
            log.info("Seller does not exists with this email.");
            return new ResponseEntity<>("Seller does not exists with this email.", HttpStatus.NOT_FOUND);
        }
    }
    //function for update-profile API
    @Override
    public ResponseEntity<String> updateSellerProfile(Authentication authentication, UpdateSellerDto updateSellerDto){
        String email=authentication.getName();
        if(sellerRepository.existsByEmail(email)){
            log.info("User exists with this mail");
            Seller seller=sellerRepository.findByEmail(email).orElseThrow(() -> new IllegalStateException("Invalid User!"));
            if(updateSellerDto.getFirstName()!=null) {
                seller.setFirstName(updateSellerDto.getFirstName());
            }

            if(updateSellerDto.getLastName()!=null) {
                seller.setLastName((updateSellerDto.getLastName()));
            }

            if(updateSellerDto.getMiddleName()!=null) {
                seller.setMiddleName(updateSellerDto.getMiddleName());
            }

            //Seller seller=sellerRepository.getSellerByUserId(user.getId());

            if(updateSellerDto.getCompanyContact()!=null) {
                seller.setCompanyContact(updateSellerDto.getCompanyContact());
            }

            if(updateSellerDto.getCompanyName()!=null) {
                seller.setCompanyName(updateSellerDto.getCompanyName());
            }
            sellerRepository.save(seller);
           // sellerRepository.save(seller);
            log.info("user profile updated!");
            /*send email*/
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            /*send mail to admin*/
            mailMessage.setSubject("Profile Updated");
            mailMessage.setText("Your profile is updated, If not done  by you contact Admin asap.\n Thanks.");
            mailMessage.setTo(seller.getEmail());
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
        Locale locale= LocaleContextHolder.getLocale();
        String email=authentication.getName();
        if(userRepository.existsByEmail(email)){
            UserEntity user=userRepository.findByEmail(email).orElseThrow(() -> new IllegalStateException("Invalid User"));
            if (passwordEncoder.matches(changePasswordDto.getPassword(), user.getPassword())) {
                return new ResponseEntity<>(messageSource.getMessage("api.error.oldPasswordMatch",null,locale),HttpStatus.BAD_REQUEST);
            }
            if(accessTokenRepository.existsByUserId(user.getId()) > 0){
                accessTokenRepository.deleteByUserId(user.getId());
            }
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
    public ResponseEntity<String> updateSellerAddress(Authentication authentication,AddressDto addressDto) {
        String email=authentication.getName();
        if (userRepository.existsByEmail(email)) {
            UserEntity user = userRepository.findByEmail(email).orElseThrow(() -> new IllegalStateException("Invalid User!"));
            List<Address> address1=addressRepository.findByUserId(user.getId());
            log.info("user exists");

            if (address1 != null) {
                log.info("address exists");
                Address address= address1.get(0);
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
            return new ResponseEntity<>(String.format("No address exists with this email" ), HttpStatus.NOT_FOUND);
        }
    }
}
