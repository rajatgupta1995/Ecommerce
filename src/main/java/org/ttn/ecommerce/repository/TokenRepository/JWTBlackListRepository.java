package org.ttn.ecommerce.repository.TokenRepository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.ttn.ecommerce.entities.token.BlackListToken;

public interface JWTBlackListRepository extends JpaRepository<BlackListToken,Integer> {

    BlackListToken findByTokenEquals(String token);
}
