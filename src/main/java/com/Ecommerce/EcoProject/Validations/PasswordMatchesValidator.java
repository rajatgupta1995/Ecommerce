package com.Ecommerce.EcoProject.Validations;

import com.TTN.Project.entities.register.UserEntity;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PasswordMatchesValidator implements ConstraintValidator<PasswordMatches,Object> {

    @Override
    public boolean isValid(Object obj, ConstraintValidatorContext constraintValidatorContext){
        UserEntity user = (UserEntity) obj;
        return user.getPassword().equals(user.getConfirmPassword());
    }

}
