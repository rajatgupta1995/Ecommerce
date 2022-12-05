package org.ttn.ecommerce;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.ttn.ecommerce.entity.register.Address;
import org.ttn.ecommerce.entity.register.Role;
import org.ttn.ecommerce.entity.register.UserEntity;
import org.ttn.ecommerce.repository.registerrepository.RoleRepository;
import org.ttn.ecommerce.repository.registerrepository.UserRepository;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Transactional
@Component
public class bootstrap implements ApplicationRunner {

    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        System.out.println(roleRepository.count());

        if(roleRepository.count()<1){
            Role admin =new Role();
            admin.setAuthority("ROLE_ADMIN");

            Role customer = new Role();
            customer.setAuthority("ROLE_CUSTOMER");

            Role seller = new Role();
            seller.setAuthority("ROLE_SELLER");

            roleRepository.save(admin);
            roleRepository.save(customer);
            roleRepository.save(seller);

        }
        if(userRepository.count()<1){

            Set<Address> addresses = new HashSet<>();
            Address address = new Address();
            address.setCity("Saharanpur");
            address.setState("UttarPradesh");
            address.setCountry("India");
            address.setAddressLine("Raghav puram colony");
            address.setLabel("bhuteshwar mandir");
            address.setZipCode("247001");

            UserEntity userEntity =new UserEntity();
            userEntity.setFirstName("Rajat");
            userEntity.setLastName("gupta");
            userEntity.setActive(true);
            userEntity.setEmail("rajat.gupta1@tothenew.com");
            userEntity.setDeleted(false);
            userEntity.setExpired(false);
            userEntity.setLocked(false);
            userEntity.setInvalidAttemptCount(0);
            userEntity.setPassword(passwordEncoder.encode("Admin@123"));

            address.setUserEntity(userEntity);

            addresses.add(address);
            userEntity.setAddresses(addresses);

            Role roles = roleRepository.findByAuthority("ROLE_ADMIN").get();
            userEntity.setRoles(Collections.singletonList(roles));

            userRepository.save(userEntity);
        }
    }
}