package org.ttn.ecommerce.dto.updateDto;

import lombok.Data;
import org.ttn.ecommerce.Validations.Password;

import javax.validation.constraints.NotBlank;

@Data
//@PasswordMatches
public class ChangePasswordDto {

    @Password
    private String password;

    @NotBlank(message = "Confirm Password and Password must be same")
    @Password
    private String confirmPassword;
}
