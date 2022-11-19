package com.Ecommerce.EcoProject.Validations;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PhoneValidator implements ConstraintValidator<Phone,String> {

    @Override
    public boolean isValid(String PhoneValue, ConstraintValidatorContext constraintValidatorContext){
        return PhoneValue.matches
                ("^\\s*(?:\\+?(\\d{1,3}))?[-. (]*(\\d{3})[-. )]*(\\d{3})[-. ]*(\\d{4})(?: *x(\\d+))?\\s*$") ? true :false;
    }

}
