package org.ttn.ecommerce.services.Impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.ttn.ecommerce.entities.token.RefreshToken;
import org.ttn.ecommerce.entities.register.UserEntity;
import org.ttn.ecommerce.entities.token.ActivateUserToken;
import org.ttn.ecommerce.exception.TokenExpiredException;
import org.ttn.ecommerce.repository.TokenRepository.RefreshTokenRepository;
import org.ttn.ecommerce.repository.TokenRepository.ActivationTokenRepository;
import org.ttn.ecommerce.repository.RegisterRepository.UserRepository;
import org.ttn.ecommerce.security.SecurityConstants;
import org.ttn.ecommerce.services.TokenService;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@Transactional
public class TokenServiceImpl implements TokenService {
    @Autowired
    ActivationTokenRepository activationTokenRepository;
    @Autowired
    RefreshTokenRepository refreshTokenRepository;
    @Autowired
    UserRepository userRepository;

    @Override
    public String generateRegisterToken(UserEntity userEntity){
        String registerToken = UUID.randomUUID().toString();
        ActivateUserToken activateUserToken = new ActivateUserToken();
        activateUserToken.setToken(registerToken);
        activateUserToken.setCreatedAt(LocalDateTime.now());
        activateUserToken.setExpireAt(LocalDateTime.now().plusMinutes(SecurityConstants.REGISTER_TOKEN_EXPIRE_MIN));

        /*changed here*/
        activateUserToken.setUserEntity(userEntity);

        activationTokenRepository.save(activateUserToken);
        return registerToken;
    }

    @Override
    public RefreshToken generateRefreshToken(UserEntity userEntity){

        countAndDeleteRefreshToken(userEntity.getId());
        String refreshToken = UUID.randomUUID().toString();
        RefreshToken refreshTokenObj = new RefreshToken();
        refreshTokenObj.setToken(refreshToken);
        refreshTokenObj.setExpireAt(LocalDateTime.now().plusHours(SecurityConstants.REFRESH_TOKEN_EXPIRE_HOURS));
        refreshTokenObj.setUserEntity(userEntity);
        return refreshTokenObj;

    }

    @Override
    @Transactional
    public void countAndDeleteRefreshToken(long user_id){
        long count = refreshTokenRepository.findByUserEntity(user_id);
        if(count >=1){
             refreshTokenRepository.deleteByUserId(user_id);
        }
    }

    @Override
    @Transactional
    public String confirmAccount(Long id, String token){
        Optional<ActivateUserToken> activateUserToken = activationTokenRepository.findByTokenAndUserEntity(token,id);
        if(activateUserToken.isPresent()){
            if(activateUserToken.get().getUserEntity().isActive()){
                log.info("Email already confirmed");
                return "Email Already confirmed";
            }
            LocalDateTime expireAt = activateUserToken.get().getExpireAt();
            if(expireAt.isBefore(LocalDateTime.now())){
                throw new TokenExpiredException("Token has been expired");
            }
            //activationTokenRepository.confirmUserBytoken(token,LocalDateTime.now());

            userRepository.activateUserById(id);
            activationTokenRepository.deleteById(activateUserToken.get().getId());
            return "Account Activated";
        }else{
            log.info("Invalid Token");
            return "Invalid Token";
        }
    }

    @Override
    public String getJWTFromRequest(HttpServletRequest request){
        String bearerToken = request.getHeader("Authorization");
        if(StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer")){
            return bearerToken.substring(7,bearerToken.length());
        }
        return null;
    }

}
