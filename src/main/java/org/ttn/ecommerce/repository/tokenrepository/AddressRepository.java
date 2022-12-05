package org.ttn.ecommerce.repository.tokenrepository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.ttn.ecommerce.entity.register.Address;

import java.util.List;


@Repository
public interface AddressRepository extends JpaRepository<Address,Long> {
    List<Address> findByid(Long id);

    @Modifying
    @Transactional
    void deleteById(Long id);

    @Query(value = "SELECT * from address WHERE user_id = ?1", nativeQuery = true)
    List<Address> findByUserId(Long id);


    @Modifying
    @Query(value = "delete from address where id = :id",nativeQuery = true)
    void deleteByAddressId(@Param("id") Long id);
}
