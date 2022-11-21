package org.ttn.ecommerce.repository.TokenRepository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.ttn.ecommerce.entities.token.Token;

import java.util.Optional;

@Repository
public interface AccessTokenRepository extends JpaRepository<Token,Long> {

    Optional<Token> findByToken(String accessToken);
}
