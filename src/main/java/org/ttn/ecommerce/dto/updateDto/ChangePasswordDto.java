package org.ttn.ecommerce.dto.updateDto;

import lombok.Data;
import org.ttn.ecommerce.Validations.Password;
import org.ttn.ecommerce.Validations.PasswordMatches;

import javax.validation.constraints.NotBlank;

@Data
@PasswordMatches
public class ChangePasswordDto {
    @NotBlank(message = "Access token cannot be blank")
    private String accessToken;

    @Password
    private String password;

    @NotBlank(message = "Confirm Password should be same to Password")
    private String confirmPassword;
}
