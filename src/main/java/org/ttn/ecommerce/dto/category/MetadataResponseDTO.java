package org.ttn.ecommerce.dto.category;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MetadataResponseDTO {
    private Long metadataId;
    private String fieldName;
    private String possibleValues;
}
