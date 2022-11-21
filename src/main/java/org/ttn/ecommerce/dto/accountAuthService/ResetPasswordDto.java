package org.ttn.ecommerce.dto.accountAuthService;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.ttn.ecommerce.Validations.Password;
import org.ttn.ecommerce.Validations.PasswordMatches;

@Data
@PasswordMatches
public class ResetPasswordDto {

    private String email;

    @Password
    private String password;

    private String confirmPassword;
    private String token;

}
