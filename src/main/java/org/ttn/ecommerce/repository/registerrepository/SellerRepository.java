package org.ttn.ecommerce.repository.registerrepository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.ttn.ecommerce.entity.register.Seller;

import javax.transaction.Transactional;
import java.util.Optional;

@Repository
@Transactional
public interface SellerRepository extends JpaRepository<Seller,Long> {
    Optional<Seller> findByEmail(String email);

    boolean existsByEmail(String email);

    @Query(value = "select * from seller where user_id=?1",nativeQuery = true)
    Seller getSellerByUserId(Long id);

    @Query(value = "SELECT a.company_contact from seller a WHERE a.user_id = ?1", nativeQuery = true)
    String getCompanyContactOfUserId(Long id);

    @Query(value = "SELECT a.company_name from seller a WHERE a.user_id = ?1", nativeQuery = true)
    String getCompanyNameOfUserId(Long id);

    @Query(value = "SELECT a.gst from seller a WHERE a.user_id = ?1", nativeQuery = true)
    String getGstNumberOfUserId(Long id);


}

