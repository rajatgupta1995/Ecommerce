package org.ttn.ecommerce.controller.auditing;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

public class AuditorAwareImpl implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        AppUser appUser = (AppUser) authentication.getPrincipal();
//        String firstName = appUser.getFirstName();
        String firstName;
        if(authentication == null){
            firstName="Anonymous user";
        }else {
             firstName = authentication.getName();
        }
        return Optional.ofNullable(firstName);
    }
}