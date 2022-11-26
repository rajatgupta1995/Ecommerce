package org.ttn.ecommerce.repository.RegisterRepository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.ttn.ecommerce.entities.register.Customer;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer,Long> {
    Optional<Customer> findByEmail(String email);

    boolean existsByEmail(String email);

}
