package org.ttn.ecommerce.dto.updateDto;

import lombok.Data;

@Data
public class AddressDto {
    private Long address_id;
    private String addressLine;
    private String city;
    private String state;
    private String country;
    private String ZipCode;
    private String label;
}