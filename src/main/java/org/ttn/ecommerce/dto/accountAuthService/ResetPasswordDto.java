package org.ttn.ecommerce.dto.accountAuthService;

import lombok.Data;
import org.ttn.ecommerce.Validations.Password;
import org.ttn.ecommerce.Validations.PasswordMatches;

import javax.validation.constraints.Email;

@Data
public class ResetPasswordDto {
    @Email
    private String email;

    @Password
    private String password;
    @Password
    private String confirmPassword;
    private String token;

}
