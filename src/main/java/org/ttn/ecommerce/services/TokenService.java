package org.ttn.ecommerce.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.ttn.ecommerce.entities.register.Customer;
import org.ttn.ecommerce.entities.token.RefreshToken;
import org.ttn.ecommerce.entities.register.UserEntity;
import org.ttn.ecommerce.entities.token.ActivateUserToken;
import org.ttn.ecommerce.repository.TokenRepository.RefreshTokenRepository;
import org.ttn.ecommerce.repository.TokenRepository.RegisterUserRepository;
import org.ttn.ecommerce.repository.UserRepository;
import org.ttn.ecommerce.security.SecurityConstants;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class TokenService {

    @Autowired
    RegisterUserRepository registerUserRepository;

    @Autowired
    RefreshTokenRepository refreshTokenRepository;

    @Autowired
    UserRepository userRepository;

    public String generateRegisterToken(UserEntity userEntity){
        String registerToken = UUID.randomUUID().toString();
        ActivateUserToken activateUserToken = new ActivateUserToken();
        activateUserToken.setToken(registerToken);
        activateUserToken.setCreatedAt(LocalDateTime.now());
        activateUserToken.setExpireAt(LocalDateTime.now().plusMinutes(SecurityConstants.REGISTER_TOKEN_EXPIRE_MIN));


        /*changed here*/
        activateUserToken.setUserEntity(userEntity);


        registerUserRepository.save(activateUserToken);
        return registerToken;
    }

    public RefreshToken generateRefreshToken(UserEntity userEntity){

        countAndDeleteRefreshToken(userEntity.getId());
        String refreshToken = UUID.randomUUID().toString();
        RefreshToken refreshTokenObj = new RefreshToken();
        refreshTokenObj.setToken(refreshToken);
        refreshTokenObj.setExpireAt(LocalDateTime.now().plusHours(SecurityConstants.REFRESH_TOKEN_EXPIRE_HOURS));
        refreshTokenObj.setUserEntity(userEntity);
        return refreshTokenObj;
    }

    @Transactional
    public void countAndDeleteRefreshToken(long user_id){

        long count = refreshTokenRepository.findByUserEntity(user_id);
        if(count >=1){
             refreshTokenRepository.deleteByUserId(user_id);
        }
    }

    @Transactional
    public String  confirmAccount(Long id, String token){
        Optional<ActivateUserToken> activateUserToken = registerUserRepository.findByTokenAndUserEntity(token,id);
        if(activateUserToken.isPresent()){

            System.out.println(activateUserToken.get().getToken());
            if(activateUserToken.get().getActivatedAt() !=null){
                return "Email Already confirmed";
            }
            LocalDateTime expireAt = activateUserToken.get().getExpireAt();
            if(expireAt.isBefore(LocalDateTime.now())){
                return "Token Expired";
            }

            registerUserRepository.confirmUserBytoken(token,LocalDateTime.now());
            userRepository.activateUserById(id);

            return "Account Activated";


        }else{
            return  "token not found";
        }
    }



}
