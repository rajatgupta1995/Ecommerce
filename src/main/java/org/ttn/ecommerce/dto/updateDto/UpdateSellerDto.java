package org.ttn.ecommerce.dto.updateDto;

import lombok.Data;
import org.ttn.ecommerce.Validations.Phone;
import javax.validation.constraints.Pattern;

@Data
public class UpdateSellerDto {

    //@NotBlank(message = "FirstName cannot be Empty")
    private String firstName;

    //@NotBlank(message = "LastName cannot be Empty")
    private String lastName;

    private String middleName;

    @Phone
    //@NotBlank(message = "Phone number cannot be empty")
    private String companyContact;

//    @Email
//    //@NotBlank(message = "Email can't be empty")
//    private String email;


    //@NotBlank(message = "Company name can't be empty")
    @Pattern(regexp = "^[a-zA-Z0-9 ]*$",flags = Pattern.Flag.CASE_INSENSITIVE,message = "Company name should be unique")
    private String companyName;

//
//    @Size(min = 15, max = 15)
//    @NotBlank(message = "GST number cannot be empty")
//    private String gstNumber;

}


