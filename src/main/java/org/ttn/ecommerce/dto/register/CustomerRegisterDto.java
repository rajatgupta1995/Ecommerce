package org.ttn.ecommerce.dto.register;

import lombok.Data;
import org.ttn.ecommerce.Validations.Password;
import org.ttn.ecommerce.Validations.PasswordMatches;
import org.ttn.ecommerce.Validations.Phone;
import org.ttn.ecommerce.Validations.UniqueEmail;

import javax.validation.constraints.*;

@Data
@PasswordMatches
public class CustomerRegisterDto {


    private String firstName;
    private String lastName;
    private String middleName;

    @Phone
    private String contact;

    @Email
    @UniqueEmail
    @NotBlank(message = "Email can not be empty")
    private String email;

    @NotBlank(message = "Password can not be empty")
    @Password
    private String password;

    private String confirmPassword;
}
