package org.ttn.ecommerce.dto;

import lombok.Data;
import org.ttn.ecommerce.Validations.Phone;
import org.ttn.ecommerce.dto.updateDto.AddressDto;

import java.util.List;

@Data
public class CustomerDto {
    private Long id;
    private String firstName;
    private String middleName;
    private String lastName;
    private String contact;

    private String email;

    private boolean active;
    private List<AddressDto> addressDto;
}
