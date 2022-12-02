package org.ttn.ecommerce.services;

import org.springframework.transaction.annotation.Transactional;
import org.ttn.ecommerce.entities.register.UserEntity;
import org.ttn.ecommerce.entities.token.RefreshToken;

import javax.servlet.http.HttpServletRequest;

public interface TokenService {
    String generateRegisterToken(UserEntity userEntity);

    RefreshToken generateRefreshToken(UserEntity userEntity);

    @Transactional
    void countAndDeleteRefreshToken(long user_id);

    @Transactional
    String confirmAccount(Long id, String token);

    String getJWTFromRequest(HttpServletRequest request);
}
