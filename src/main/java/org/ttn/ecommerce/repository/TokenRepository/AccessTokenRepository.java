package org.ttn.ecommerce.repository.TokenRepository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.ttn.ecommerce.entities.token.Token;

import java.util.Optional;

@Repository
public interface AccessTokenRepository extends JpaRepository<Token,Long> {

    Optional<Token> findByToken(String accessToken);
    @Query(value = "DELETE from access_token where user_id = :id",nativeQuery = true)
    @Modifying
    public void deleteByUserId(Long id);

    @Query(value = "select count(id) from access_token where user_id = :id",nativeQuery = true)
    Integer existsByUserId(Long id);

}
