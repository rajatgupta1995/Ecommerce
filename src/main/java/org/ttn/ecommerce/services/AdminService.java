package org.ttn.ecommerce.services;

import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJacksonValue;

public interface AdminService {
    ResponseEntity<?> activateUser(Long user_id);

    ResponseEntity<?> deactivateUser(Long user_id);

    MappingJacksonValue listAllCustomer(String page, String size, String sortBy);

    MappingJacksonValue listAllSeller(String page, String size, String sortBy);
}
