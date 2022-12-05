package org.ttn.ecommerce.services.Impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.ttn.ecommerce.dto.accountAuthService.ResetPasswordDto;
import org.ttn.ecommerce.entity.register.UserEntity;
import org.ttn.ecommerce.entity.token.ForgetPasswordToken;
import org.ttn.ecommerce.exception.TokenExpiredException;
import org.ttn.ecommerce.exception.UserNotFoundException;
import org.ttn.ecommerce.repository.registerrepository.UserRepository;
import org.ttn.ecommerce.repository.tokenrepository.AccessTokenRepository;
import org.ttn.ecommerce.repository.tokenrepository.ForgetPasswordRepository;
import org.ttn.ecommerce.security.SecurityConstants;
import org.ttn.ecommerce.services.PasswordService;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
@Slf4j
public class PasswordServiceImpl implements PasswordService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ForgetPasswordRepository forgetPasswordRepository;
    @Autowired
    private EmailService emailService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AccessTokenRepository accessTokenRepository;

    @Override
    public ResponseEntity<String> forgetPassword(String email){
        Optional<UserEntity> userEntity = userRepository.findByEmail(email);
        /*Checking if user is present in userRepository or not*/
        if(userEntity.isPresent()){
            /*Checking if user present isActive*/
            if(!userEntity.get().isActive()){
                throw new TokenExpiredException("User is not active");
            }

            /*Fetching forget token from forgetPasswordRepository if present in forget_password_token table with this user_id*/
            Optional<ForgetPasswordToken> forgetPasswordToken = forgetPasswordRepository.getTokenByUserId(userEntity.get().getId());

            /* If forget password token already exist*/
            if(forgetPasswordToken.isPresent()){
                forgetPasswordRepository.deleteByTokenId(forgetPasswordToken.get().getId());
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

    @Override
    public boolean verifyToken(LocalDateTime localDateTime){

        Duration timeDiff = Duration.between(localDateTime,LocalDateTime.now());
        return timeDiff.toMinutes() >= SecurityConstants.RESET_PASS_EXPIRE_MINUTES;

    }

    @Override
    public ForgetPasswordToken forgerPassGenerate(UserEntity userEntity){
        String token = UUID.randomUUID().toString();
        ForgetPasswordToken forgetPasswordToken= new ForgetPasswordToken();
        forgetPasswordToken.setToken(token);
        forgetPasswordToken.setUserEntity(userEntity);
        forgetPasswordToken.setCreatedAt(LocalDateTime.now());
        forgetPasswordToken.setExpireAt(LocalDateTime.now().plusMinutes(SecurityConstants.FORGET_PASS_EXPIRE_MINUTES));

        return forgetPasswordToken;
    }

    @Override
    @Transactional
    public ResponseEntity<String> resetUserPassword(ResetPasswordDto resetPasswordDto) {

        Optional<UserEntity> userEntity = userRepository.findByEmail(resetPasswordDto.getEmail());
        /*User does not exist with this mail*/
        if(!userEntity.isPresent()){
            log.info("User with this mail does not exist.");
            throw new UserNotFoundException("User with this email : " + resetPasswordDto.getEmail() + "does not exist.");
        }
        /*Password and Confirm Password does not match*/
        if(!resetPasswordDto.getPassword().equals(resetPasswordDto.getConfirmPassword())){
            log.info("Password and confirmPassword does not match.");
            return new ResponseEntity<>("Password and confirmPassword does not match.",HttpStatus.UNAUTHORIZED);
        }
        Optional<ForgetPasswordToken> forgetPasswordToken = forgetPasswordRepository.findByToken(resetPasswordDto.getToken());
        UserEntity user=userEntity.get();
        /*Checking token present in forgetTokenRepository or not*/
        if(forgetPasswordToken.isPresent() && forgetPasswordToken.get().getUserEntity().getEmail() == user.getEmail()){
            ForgetPasswordToken forgetPasswordToken_=forgetPasswordToken.get();
            /*Checking token expiry*/
            if(verifyToken(forgetPasswordToken_.getExpireAt())){
                forgetPasswordRepository.deleteByTokenId(forgetPasswordToken_.getId());
                throw new TokenExpiredException("Token has been expired, request for new Token via Forgot Password Link");
            }
            else{
                user.setPassword(passwordEncoder.encode(resetPasswordDto.getPassword()));
                userRepository.save(user);
                /*Bonus*/
                if (passwordEncoder.matches(resetPasswordDto.getPassword(), user.getPassword())) {
                    return new ResponseEntity<>("Old password and new Password are same",HttpStatus.BAD_REQUEST);
                }
                if(accessTokenRepository.existsByUserId(user.getId()) > 0){
                    accessTokenRepository.deleteByUserId(user.getId());
                }
                /* Send Email */
                SimpleMailMessage mailMessage = new SimpleMailMessage();
                String toEmail=user.getEmail();
                String subject="Password Updated";
                String message="Your password has been changed successfully!!";
                emailService.sendEmail(toEmail,subject,message);

                /* delete this forgetPasswordToken from repository. */
                forgetPasswordRepository.deleteByTokenId(forgetPasswordToken_.getId());

                return new ResponseEntity<>("Password changed",HttpStatus.OK);
            }
        }else{
            throw new TokenExpiredException("Invalid Token or wrong email");
        }
    }
}
