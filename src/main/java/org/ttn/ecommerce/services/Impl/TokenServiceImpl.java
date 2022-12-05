package org.ttn.ecommerce.services.Impl;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.ttn.ecommerce.dto.accountAuthService.AuthResponseDto;
import org.ttn.ecommerce.entity.register.Role;
import org.ttn.ecommerce.entity.register.UserEntity;
import org.ttn.ecommerce.entity.token.ActivateUserToken;
import org.ttn.ecommerce.entity.token.RefreshToken;
import org.ttn.ecommerce.entity.token.Token;
import org.ttn.ecommerce.exception.ResourceNotFoundException;
import org.ttn.ecommerce.exception.TokenExpiredException;
import org.ttn.ecommerce.repository.registerrepository.UserRepository;
import org.ttn.ecommerce.repository.tokenrepository.AccessTokenRepository;
import org.ttn.ecommerce.repository.tokenrepository.ActivationTokenRepository;
import org.ttn.ecommerce.repository.tokenrepository.RefreshTokenRepository;
import org.ttn.ecommerce.security.SecurityConstants;
import org.ttn.ecommerce.services.TokenService;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@Transactional
public class TokenServiceImpl implements TokenService {
    @Autowired
    private ActivationTokenRepository activationTokenRepository;
    @Autowired
    private RefreshTokenRepository refreshTokenRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AccessTokenRepository accessTokenRepository;

    @Override
    public String generateRegisterToken(UserEntity userEntity) {
        String registerToken = UUID.randomUUID().toString();
        ActivateUserToken activateUserToken = new ActivateUserToken();
        activateUserToken.setToken(registerToken);
        activateUserToken.setCreatedAt(LocalDateTime.now());
        activateUserToken.setExpireAt(LocalDateTime.now().plusMinutes(SecurityConstants.REGISTER_TOKEN_EXPIRE_MIN));

        activateUserToken.setUserEntity(userEntity);

        activationTokenRepository.save(activateUserToken);
        return registerToken;
    }

    @Override
    public RefreshToken generateRefreshToken(UserEntity userEntity) {

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
    public void countAndDeleteRefreshToken(long user_id) {
        long count = refreshTokenRepository.findByUserEntity(user_id);
        if (count >= 1) {
            refreshTokenRepository.deleteByUserId(user_id);
        }
    }

    @Override
    @Transactional
    public String activateAccount(Long id, String token) {
        Optional<ActivateUserToken> activateUserToken = activationTokenRepository.findByTokenAndUserEntity(token, id);
        if (activateUserToken.isPresent()) {
            if (activateUserToken.get().getUserEntity().isActive()) {
                log.info("Email already confirmed");
                return "Email Already confirmed";
            }
            LocalDateTime expireAt = activateUserToken.get().getExpireAt();
            if (expireAt.isBefore(LocalDateTime.now())) {
                throw new TokenExpiredException("Token has been expired");
            }
            //activationTokenRepository.confirmUserBytoken(token,LocalDateTime.now());

            userRepository.activateUserById(id);
            activationTokenRepository.deleteById(activateUserToken.get().getId());
            log.info("Account Activated");
            return "Account Activated";
        } else {
            log.info("Invalid Token");
            return "Invalid Token";
        }
    }

    @Override
    public String getJWTFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer")) {
            return bearerToken.substring(7, bearerToken.length());
        }
        return null;
    }

    @Override
    public ResponseEntity<?> newAccessToken(String userRefreshToken) {
        /*check refresh token exists or not*/
        Optional<RefreshToken> refreshToken = refreshTokenRepository.findByToken(userRefreshToken);
        if(!refreshToken.isPresent())
                throw new ResourceNotFoundException("Refresh token is invalid or it does not exists." + "\n" + "Please check.");

        UserEntity userEntity = refreshToken.get().getUserEntity();
        LocalDateTime expireDateTime = refreshToken.get().getExpireAt();

        /* Check Refresh Token Expired. */
        if (expireDateTime.isBefore(LocalDateTime.now())) {
            throw new TokenExpiredException("Refresh Token is expired." + "\n" + "Please login again to get the new Access Token.");
        } else {
            String userEmail = userEntity.getEmail();
            Date currentDate = new Date();
            Date expireDate = new Date(currentDate.getTime() + SecurityConstants.JWT_EXPIRATION);

            String role = "";
            /* fetch role from user */
            List<Role> roles = (List<Role>) userEntity.getRoles();
            for (Role role1 : roles) {
                if (role1.getAuthority().equals("ROLE_SELLER")) {
                    role = "SELLER";
                }
                if (role1.getAuthority().equals("ROLE_CUSTOMER")) {
                    role = "CUSTOMER";
                }
                if (role1.getAuthority().equals("ROLE_ADMIN")) {
                    role = "ADMIN";
                }
            }

            /* Generating Token */
            String token = Jwts.builder()
                    .setSubject(userEmail)
                    .setIssuedAt(new Date())
                    .claim("ROLE", role)
                    .setExpiration(expireDate)
                    .signWith(SignatureAlgorithm.HS512, SecurityConstants.JWT_SECRET)
                    .compact();

            /* Access Token */
            Token accessToken = new Token();
            accessToken.setUserEntity(userEntity);
            accessToken.setToken(token);
            accessToken.setCreatedAt(LocalDateTime.now());
            accessToken.setExpiredAt(LocalDateTime.now().plusMinutes(SecurityConstants.JWT_EXPIRATION));
            accessTokenRepository.save(accessToken);

            /* return access token and refresh token */
            return new ResponseEntity<>(new AuthResponseDto(accessToken.getToken(), userRefreshToken), HttpStatus.CREATED);
        }
    }
}

