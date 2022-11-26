package org.ttn.ecommerce.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.ttn.ecommerce.dto.accountAuthService.ResetPasswordDto;
import org.ttn.ecommerce.entities.register.UserEntity;
import org.ttn.ecommerce.entities.token.ForgetPasswordToken;
import org.ttn.ecommerce.exception.TokenExpiredException;
import org.ttn.ecommerce.exception.UserNotFoundException;
import org.ttn.ecommerce.repository.TokenRepository.AccessTokenRepository;
import org.ttn.ecommerce.repository.TokenRepository.ForgetPasswordRepository;
import org.ttn.ecommerce.repository.TokenRepository.JWTBlackListRepository;
import org.ttn.ecommerce.repository.RegisterRepository.UserRepository;
import org.ttn.ecommerce.security.SecurityConstants;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
@Slf4j
public class PasswordService {
    @Autowired
    UserRepository userRepository;
    @Autowired
    ForgetPasswordRepository forgetPasswordRepository;
    @Autowired
    EmailService emailService;
    @Autowired
    PasswordEncoder passwordEncoder;

    public ResponseEntity<String> forgetPassword(String email){
        Optional<UserEntity> userEntity = userRepository.findByEmail(email);
        /*Checking if user is present in userRepository or not*/
        if(userEntity.isPresent()){
            /*Checking if user present isActive*/
            if(!userEntity.get().isActive()){
                throw new TokenExpiredException("User is not active");
            }
            /*Fetching forget token from forgetPasswordRepository if present in forget_password_token table with this user_id*/
            ForgetPasswordToken forgetPasswordToken = forgetPasswordRepository.getTokenByUserId(userEntity.get().getId()).get();

            /* If forget password token already exist*/
            if(forgetPasswordToken!=null){
//                LocalDateTime expireAt=forgetPasswordToken.getExpireAt();
//                if(expireAt.isBefore(LocalDateTime.now())){
//
//                }
                forgetPasswordRepository.deleteByTokenId(forgetPasswordToken.getId());
            }

            /*Generating a new forget token*/
            ForgetPasswordToken forgetPassword = forgerPassGenerate(userEntity.get());
            forgetPasswordRepository.save(forgetPassword);

            /* Send Mail with rest password Link */
            String subject="Reset Password";
            String message="To reset your password , please click the link below within 15 minutes \n"
            + "http://127.0.0.1:6640/api/auth/forget-password/"
            +forgetPassword.getToken();
            String toEmail=userEntity.get().getEmail();
            emailService.sendEmail(toEmail,subject,message);

            return new ResponseEntity<>("ResetPassword Token Generated please check your email and verify it within 15 minutes",HttpStatus.OK);
        }else{
            log.info("User with this mail does not exist.");
            throw new UserNotFoundException("User with this email : " + email + "does not exist." );
        }
    }

    public boolean verifyToken(LocalDateTime localDateTime){

        Duration timeDiff = Duration.between(localDateTime,LocalDateTime.now());
        return timeDiff.toMinutes() >= SecurityConstants.RESET_PASS_EXPIRE_MINUTES;

    }

    public ForgetPasswordToken forgerPassGenerate(UserEntity userEntity){
        String token = UUID.randomUUID().toString();
        ForgetPasswordToken forgetPasswordToken= new ForgetPasswordToken();
        forgetPasswordToken.setToken(token);
        forgetPasswordToken.setUserEntity(userEntity);
        forgetPasswordToken.setCreatedAt(LocalDateTime.now());
        forgetPasswordToken.setExpireAt(LocalDateTime.now().plusMinutes(SecurityConstants.FORGET_PASS_EXPIRE_MINUTES));

        return forgetPasswordToken;
    }

    @Transactional
    public ResponseEntity<String> resetUserPassword(ResetPasswordDto resetPasswordDto) {

        UserEntity userEntity = userRepository.findByEmail(resetPasswordDto.getEmail()).get();
        /*User does not exist with this mail*/
        if(userEntity == null){
            log.info("User with this mail does not exist.");
            throw new UserNotFoundException("User with this email : " + resetPasswordDto.getEmail() + "does not exist.");
        }
        /*Password and Confirm Password does not match*/
        if(!resetPasswordDto.getPassword().equals(resetPasswordDto.getConfirmPassword())){
            log.info("Password and confirmPassword does not match.");
            return new ResponseEntity<>("Password and confirmPassword does not match.",HttpStatus.UNAUTHORIZED);
        }
        Optional<ForgetPasswordToken> forgetPasswordToken = forgetPasswordRepository.findByToken(resetPasswordDto.getToken());

        /*Checking token present in forgetTokenRepository or not*/
        if(forgetPasswordToken.isPresent()){
            /*Checking token expiry*/
            if(verifyToken(forgetPasswordToken.get().getExpireAt())){
                forgetPasswordRepository.deleteByTokenId(forgetPasswordToken.get().getId());
                throw new TokenExpiredException("Token has been expired, request for new Token via Forgot Password Link");
            }
            else{
                userEntity.setPassword(passwordEncoder.encode(resetPasswordDto.getPassword()));
                userRepository.save(userEntity);

                /* Send Email */
                SimpleMailMessage mailMessage = new SimpleMailMessage();
                String toEmail=userEntity.getEmail();
                String subject="Password Updated";
                String message="Your password has been changed successfully!!";
                emailService.sendEmail(toEmail,subject,message);

                /* delete this forgetPasswordToken from repository. */
                forgetPasswordRepository.deleteByTokenId(forgetPasswordToken.get().getId());

                return new ResponseEntity<>("Password changed",HttpStatus.OK);
            }
        }else{
            throw new TokenExpiredException("Invalid Token");
        }
    }
}
