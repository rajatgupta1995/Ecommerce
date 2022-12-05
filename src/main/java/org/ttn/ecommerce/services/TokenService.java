package org.ttn.ecommerce.services;

import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.ttn.ecommerce.entity.register.UserEntity;
import org.ttn.ecommerce.entity.token.RefreshToken;

import javax.servlet.http.HttpServletRequest;

public interface TokenService {
    String generateRegisterToken(UserEntity userEntity);

    RefreshToken generateRefreshToken(UserEntity userEntity);

    @Transactional
    void countAndDeleteRefreshToken(long user_id);

    @Transactional
    String activateAccount(Long id, String token);

    String getJWTFromRequest(HttpServletRequest request);

    ResponseEntity<?> newAccessToken(String refreshToken);
}
