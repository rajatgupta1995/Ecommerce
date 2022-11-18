package com.Ecommerce.EcoProject.repository;

import com.Ecommerce.EcoProject.Model.register.Token;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TokenRepository extends JpaRepository<Token,Long> {

        Token findByToken(String token);

        Token  findByEmail(String email);

        Token findByForgotPassToken(String token);

}

