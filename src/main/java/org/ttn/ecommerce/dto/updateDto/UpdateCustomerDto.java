package org.ttn.ecommerce.dto.updateDto;

import lombok.Data;
import org.ttn.ecommerce.Validations.Phone;

@Data
public class UpdateCustomerDto {
    private String firstName;
    private String middleName;
    private String lastName;
    @Phone
    private String contact;

}