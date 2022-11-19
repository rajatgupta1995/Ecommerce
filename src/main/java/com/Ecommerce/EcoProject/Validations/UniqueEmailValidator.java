package com.Ecommerce.EcoProject.Validations;

import com.TTN.Project.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;


public class UniqueEmailValidator implements ConstraintValidator<UniqueEmail,String> {

    private UserRepository userRepository;

    @Autowired
    public UniqueEmailValidator(UserRepository userRepository) {
        this.userRepository = userRepository;
    }



    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext){
        System.out.println(value);
        return false;
//        Optional<UserEntity> userData =userRepository.findByemail(value);
//        if(userData.isPresent()){
//            System.out.println(value);
//            return false;
//        }else{
//            return true;
//        }
    }

}
