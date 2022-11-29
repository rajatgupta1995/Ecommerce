package org.ttn.ecommerce.dto;

import lombok.Data;
import org.ttn.ecommerce.Validations.Password;

import javax.validation.constraints.Email;

@Data
public class LoginDto {
    @Email
    private String email;
//    @Password
    private String password;

}
