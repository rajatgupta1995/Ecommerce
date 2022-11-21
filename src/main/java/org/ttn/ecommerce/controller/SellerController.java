package org.ttn.ecommerce.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.ttn.ecommerce.entities.token.BlackListToken;
import org.ttn.ecommerce.entities.token.Token;
import org.ttn.ecommerce.repository.TokenRepository.AccessTokenRepository;
import org.ttn.ecommerce.repository.TokenRepository.JWTBlackListRepository;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping(value = "/seller")
public class SellerController {

    @Autowired
    JWTBlackListRepository jwtBlackListRepository;

    @Autowired
    AccessTokenRepository accessTokenRepo;

    @PreAuthorize("hasRole('ROLE_SELLER')")
    @GetMapping("login")
    public String getData(){
        return "a";
    }


    @PostMapping("logout")
    @Transactional
    public String logout(HttpServletRequest request){
        String bearerToken = request.getHeader("Authorization");
        if(StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            String tokenValue = bearerToken.substring(7, bearerToken.length());
            //System.out.println(tokenValue);
            BlackListToken jwtBlacklist = new BlackListToken();
            Token token=accessTokenRepo.findByToken(tokenValue).get();
            jwtBlacklist.setToken(token.getToken());
            jwtBlacklist.setUserEntity(token.getUserEntity());
            jwtBlackListRepository.save(jwtBlacklist);
            accessTokenRepo.delete(token);
        }
        return "Logged out successfully";
    }
}
