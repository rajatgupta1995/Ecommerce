package org.ttn.ecommerce.dto.accountAuthService;

import lombok.Data;
import org.ttn.ecommerce.Validations.Password;
import org.ttn.ecommerce.Validations.PasswordMatches;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
public class ResetPasswordDto {
    @NotBlank(message = "Email cannot be empty")
    @Email
    private String email;

    @NotBlank(message = "Password cannot be empty")
    @Password
    private String password;

    @NotBlank(message = "Confirm Password can't be empty")
    @Password
    private String confirmPassword;
    @NotBlank(message = "token cannot be empty")
    private String token;

}
