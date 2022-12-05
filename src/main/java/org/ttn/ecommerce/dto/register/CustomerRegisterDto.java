package org.ttn.ecommerce.dto.register;

import lombok.Data;
import org.ttn.ecommerce.Validations.Password;
import org.ttn.ecommerce.Validations.Phone;
import org.ttn.ecommerce.Validations.UniqueEmail;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
public class CustomerRegisterDto {

    @NotBlank(message = "firstName cannot be empty")
    private String firstName;
    @NotBlank(message = "lastName cannot be empty")
    private String lastName;
    private String middleName;

    @Phone
    @NotBlank(message = "Phone number cannot be empty")
    private String contact;

    @Email
    @UniqueEmail
    @NotBlank(message = "Email cannot be empty")
    private String email;

    @NotBlank(message = "Password cannot be empty")
    @Password
    private String password;
    @Password
    @NotBlank(message = "Confirm Password can't be empty")
    private String confirmPassword;
}
