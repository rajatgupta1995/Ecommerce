package org.ttn.ecommerce.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.ttn.ecommerce.entities.register.Seller;

import java.util.Optional;

public interface SellerRepository extends JpaRepository<Seller,Long> {
    Optional<Seller> findByEmail(String email);

    boolean existsByEmail(String email);

    @Query(value = "select * from seller where user_id:id",nativeQuery = true)
    Seller getSellerByUserId(Long id);
}
