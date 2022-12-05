package org.ttn.ecommerce.dto.register;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.ttn.ecommerce.dto.updateDto.AddressDto;

import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)//declare member variable as private
public class SellerResponseDto {

    Long id;
    String firstName;
    String lastName;
    String middleName;
    String contact;
    String email;
    boolean active;
    String imagePath;
    List<AddressDto> addressDto;


}
