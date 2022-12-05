package org.ttn.ecommerce.Validations;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PhoneValidator implements ConstraintValidator<Phone,String> {

    @Override
    public boolean isValid(String PhoneValue, ConstraintValidatorContext constraintValidatorContext) {

        if (PhoneValue == null) {
            return true;
        }
        if (PhoneValue.matches("^\\s*(?:\\+?(\\d{1,3}))?[-. (]*(\\d{3})[-. )]*(\\d{3})[-. ]*(\\d{4})(?: *x(\\d+))?\\s*$")) {
            return true;
        }
        return false;
    }

}
