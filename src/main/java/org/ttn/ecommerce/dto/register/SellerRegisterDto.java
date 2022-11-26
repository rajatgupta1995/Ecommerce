package org.ttn.ecommerce.dto.register;

import lombok.Data;
import org.ttn.ecommerce.Validations.Password;
import org.ttn.ecommerce.Validations.PasswordMatches;
import org.ttn.ecommerce.Validations.Phone;
import org.ttn.ecommerce.Validations.UniqueEmail;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;


@Data
public class SellerRegisterDto {

    private String firstName;
    private String lastName;
    private String middleName;

    @Phone
    @NotBlank(message = "Phone number can't be empty")
    private String companyContact;

    @Email
    @UniqueEmail
    @NotBlank(message = "Email can't be empty")
    private String email;

    @NotBlank(message = "Password can't be empty")
    @Password
    private String password;

    @NotBlank(message = "Company name can't be empty")
    @Pattern(regexp = "^[a-zA-Z0-9 ]*$",flags = Pattern.Flag.CASE_INSENSITIVE,message = "Company name should be unique")
    private String companyName;

    private String gstNumber;
    @Password
    @NotBlank(message = "Confirm Password can't be empty")
    private String confirmPassword;

}
