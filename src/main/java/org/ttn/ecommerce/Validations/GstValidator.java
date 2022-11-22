package org.ttn.ecommerce.Validations;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class GstValidator implements ConstraintValidator<Gst,String> {

    @Override
    public boolean isValid(String gstValue, ConstraintValidatorContext constraintValidatorContext){
        if(gstValue.matches("^[0-9]{2}[A-Z]{5}[0-9]{4}[A-Z]{1}[1-9A-Z]{1}Z[0-9A-Z]{1}$"))
            return true;
        return false;
    }
}
