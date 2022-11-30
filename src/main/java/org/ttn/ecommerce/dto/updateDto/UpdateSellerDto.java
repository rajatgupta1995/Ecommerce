package org.ttn.ecommerce.dto.updateDto;

import lombok.Data;
import org.ttn.ecommerce.Validations.Phone;

@Data
public class UpdateSellerDto {
    private String firstName;
    private String lastName;
    private String middleName;
    @Phone
    private String companyContact;
    private String companyName;
}


