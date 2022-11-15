package com.Ecommerce.EcoProject;

import com.Ecommerce.EcoProject.Model.register.Role;
import com.Ecommerce.EcoProject.repository.RoleRepository;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@AutoConfigureTestEntityManager
public class Test {
    @Autowired
    private RoleRepository roleRepository;

    @org.junit.jupiter.api.Test
    void contextLoads() {
    }

    @org.junit.jupiter.api.Test
    void testRest() {
        Optional<Role> role =roleRepository.findByAuthority("CUSTOMER");
        if(role.isPresent()){
            System.out.println(role.get().getAuthority());
        }
    }
}
