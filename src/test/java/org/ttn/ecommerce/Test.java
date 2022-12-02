package org.ttn.ecommerce;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import org.ttn.ecommerce.entities.register.Address;
import org.ttn.ecommerce.entities.register.Customer;
import org.ttn.ecommerce.entities.register.UserEntity;
import org.ttn.ecommerce.repository.RegisterRepository.CustomerRepository;
import org.ttn.ecommerce.repository.RegisterRepository.RoleRepository;
import org.ttn.ecommerce.repository.TokenRepository.RefreshTokenRepository;
import org.ttn.ecommerce.repository.RegisterRepository.UserRepository;
import org.ttn.ecommerce.services.Impl.TokenServiceImpl;

import java.util.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@AutoConfigureTestEntityManager
public class Test {
    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    RefreshTokenRepository refreshTokenRepository;

    @Autowired
    TokenServiceImpl tokenService;

    @org.junit.jupiter.api.Test
    void contextLoads() {
    }

    @org.junit.jupiter.api.Test
    @Rollback(value = false)
    void testRest() {
      /*  Optional<Role> role =roleRepository.findByAuthority("CUSTOMER");
        if(role.isPresent()){
            System.out.println(role.get().getAuthority());*/
        Set<Address> addressList = new HashSet<>();
        Address address = new Address();
        address.setAddressLine("a");
        address.setCity("hld");
        address.setState("uk");
        address.setZipCode("123");

        addressList.add(address);
        Customer customer =new Customer();
        customer.setEmail("k1233@gmail.com");
        customer.setPassword(passwordEncoder.encode("123456"));
        customer.setContact("234567890");
        customer.setAddresses(addressList);

        customerRepository.save(customer);


    }
    @org.junit.jupiter.api.Test
    @Transactional
    public void testGetData(){
        Optional<UserEntity> user = userRepository.findByEmail("k123@gmail.com");
        if(user.isPresent()){
            System.out.println(user.get().getAddresses());
        }
    }

    @org.junit.jupiter.api.Test
    public void testRefreshRepo(){
        long l =16;
       long count = refreshTokenRepository.findByUserEntity(l);
       if(count >=1){
           System.out.println("record found : " + count );
       }
    }

    @org.junit.jupiter.api.Test
    @Transactional
    @Rollback(false)
    public void testDeleteRefreshRepo(){

        refreshTokenRepository.deleteByUserId(16l);
        testRefreshRepo();
    }


    @org.junit.jupiter.api.Test
    @Transactional
    @Rollback(false)
    public void testUser(){
    long id=16;
       tokenService.confirmAccount(id,"13835945-3041-4007-9ca6-f335c9c41fc3");
    }
    }

