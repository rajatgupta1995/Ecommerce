package org.ttn.ecommerce.services;

import com.auth0.jwt.exceptions.TokenExpiredException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.ttn.ecommerce.entities.register.Address;
import org.ttn.ecommerce.entities.register.UserEntity;
import org.ttn.ecommerce.entities.token.Token;
import org.ttn.ecommerce.repository.TokenRepository.AccessTokenRepository;
import org.ttn.ecommerce.repository.TokenRepository.AddressRepository;
import org.ttn.ecommerce.repository.UserRepository;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;

@Service
@Transactional
public class CustomerService {

    @Autowired
    private AccessTokenRepository accessTokenRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    AddressRepository addressRepository;


    //view-profile

    public ResponseEntity<?> viewMyProfile(String accessToken)  {
       Token token =  accessTokenRepository.findByToken(accessToken).orElseThrow(() -> new IllegalStateException("Invalid Access Token!"));


        System.out.println(accessToken);
        System.out.println();
        LocalDateTime expiredAt = token.getExpiredAt();
        if (expiredAt.isBefore(LocalDateTime.now())) {
            Instant instant = token.getExpiredAt().toInstant(ZoneOffset.UTC);
             throw new TokenExpiredException("Expired Token",instant);
        }
        UserEntity user = userRepository.findByEmail(token.getUserEntity().getEmail()).get();
        return new ResponseEntity<>("Customer User Id: "+user.getId()+
                "\nCustomer First name: "+user.getFirstName()+"\nCustomer Last name: "+user.getLastName()+
                "\nCustomer active status: "+user.isActive()+"\nCustomer contact:" +
                " "+userRepository.getReferenceById(user.getId()), HttpStatus.OK);
    }

    public ResponseEntity<?> viewMyAddresses(String accessToken) {
       Token token =  accessTokenRepository.findByToken(accessToken).orElseThrow(() -> new IllegalStateException("Invalid Access Token!"));

        LocalDateTime expiredAt = token.getExpiredAt();
        if (expiredAt.isBefore(LocalDateTime.now())) {
            Instant instant = token.getExpiredAt().toInstant(ZoneOffset.UTC);
            throw new TokenExpiredException("Expired Token",instant);
        }
        if (userRepository.existsByEmail(token.getUserEntity().getEmail())) {
            UserEntity user = userRepository.findByEmail(token.getUserEntity().getEmail()).get();
            List<Object[]> list = addressRepository.findByid(user.getId());
            return new ResponseEntity<>(list, HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Error fetching addresses", HttpStatus.NOT_FOUND);
        }
    }

    //saving address
    public String createAddress(Long id, Address address){
        Set<Address> addressList = new HashSet<>();
        UserEntity user = userRepository.findById(id).get();
        address.setUserEntity(user);
        addressRepository.save(address);
        addressList.add(address);
        user.setAddresses((Set<Address>) addressList);
        return "Address created successfully";
    }

}
