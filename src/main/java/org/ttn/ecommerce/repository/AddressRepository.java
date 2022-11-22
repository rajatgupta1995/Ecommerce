package org.ttn.ecommerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.ttn.ecommerce.entities.register.Address;

public interface AddressRepository extends JpaRepository<Address,Long> {
}
