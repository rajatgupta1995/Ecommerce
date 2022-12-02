package org.ttn.ecommerce.services;

import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.ttn.ecommerce.dto.accountAuthService.ResetPasswordDto;
import org.ttn.ecommerce.entities.register.UserEntity;
import org.ttn.ecommerce.entities.token.ForgetPasswordToken;

import java.time.LocalDateTime;

public interface PasswordService {
    ResponseEntity<String> forgetPassword(String email);

    boolean verifyToken(LocalDateTime localDateTime);

    ForgetPasswordToken forgerPassGenerate(UserEntity userEntity);

    @Transactional
    ResponseEntity<String> resetUserPassword(ResetPasswordDto resetPasswordDto);
}
