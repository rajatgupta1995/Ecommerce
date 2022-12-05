package org.ttn.ecommerce.repository.tokenrepository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.ttn.ecommerce.entity.token.ForgetPasswordToken;

import java.util.Optional;

public interface ForgetPasswordRepository extends JpaRepository<ForgetPasswordToken,Long> {

        @Query(value = "select * from forget_password_token where user_id = :id",nativeQuery = true)
        Optional<ForgetPasswordToken> getTokenByUserId(@Param("id") Long id);

        Optional<ForgetPasswordToken> findByToken(String token);

        @Query(value = "DELETE from forget_password_token where id = :id",nativeQuery = true)
        @Modifying
        public void deleteByTokenId(Long id);
}
