package org.ttn.ecommerce.Validations;


import org.ttn.ecommerce.entity.register.UserEntity;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PasswordMatchesValidator implements ConstraintValidator<PasswordMatches,Object> {

    @Override
    public boolean isValid(Object obj, ConstraintValidatorContext constraintValidatorContext){
        UserEntity user = (UserEntity) obj;
        return user.getPassword().equals(user.getConfirmPassword());
    }

}
