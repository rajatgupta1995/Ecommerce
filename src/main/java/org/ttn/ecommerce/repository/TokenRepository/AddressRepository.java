package org.ttn.ecommerce.repository.TokenRepository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.ttn.ecommerce.entities.register.Address;

import java.util.List;

public interface AddressRepository extends JpaRepository<Address,Integer> {
    List<Object[]> findByid(Long id);
}
