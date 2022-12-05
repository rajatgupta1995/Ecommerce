package org.ttn.ecommerce.Validations;


import org.springframework.beans.factory.annotation.Autowired;
import org.ttn.ecommerce.repository.registerrepository.UserRepository;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;


public class UniqueEmailValidator implements ConstraintValidator<UniqueEmail,String> {

    @Autowired
    private UserRepository userRepository;

    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext){
        return userRepository.findByEmail(value).isPresent() ? false : true;
    }

}
