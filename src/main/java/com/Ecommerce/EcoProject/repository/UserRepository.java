package com.Ecommerce.EcoProject.repository;

import com.Ecommerce.EcoProject.Model.register.EntityUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface UserRepository  extends JpaRepository<EntityUser,Long> {
    Optional<EntityUser> findByemail(String email);

    boolean existsByemail(String email);

}
