package org.ttn.ecommerce.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ProductNotActiveException extends RuntimeException{
    public ProductNotActiveException(String message){
        super(message);
    }
}
