package org.ttn.ecommerce.repository.tokenrepository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.ttn.ecommerce.entity.token.RefreshToken;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken,Long> {
    Optional<RefreshToken> findByToken(String token);
    Optional<RefreshToken> findById(Long id);

    @Query(value="select count(user_id) from refresh_token where user_id = :uid ",nativeQuery = true)
    long findByUserEntity(@Param("uid") Long id);

    @Modifying
    @Query(value = "delete from refresh_token where user_id = :userId",nativeQuery = true)
    public void deleteByUserId(@Param("userId") Long id);

}
