package org.ttn.ecommerce.repository.TokenRepository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.ttn.ecommerce.entities.register.Address;

import java.util.List;

public interface AddressRepository extends JpaRepository<Address,Long> {
    List<Address> findByid(Long id);

    @Query(value = "SELECT * from address WHERE user_id = ?1", nativeQuery = true)
    List<Address> findByUserId(Long id);


}
